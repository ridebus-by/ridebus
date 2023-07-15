package org.xtimms.ridebus.data.updater.database

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.PowerManager
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import okhttp3.internal.http2.ErrorCode
import okhttp3.internal.http2.StreamResetException
import org.xtimms.ridebus.BuildConfig
import org.xtimms.ridebus.R
import org.xtimms.ridebus.data.database.RideBusDatabase
import org.xtimms.ridebus.data.notification.Notifications
import org.xtimms.ridebus.data.preference.PreferencesHelper
import org.xtimms.ridebus.network.GET
import org.xtimms.ridebus.network.NetworkHelper
import org.xtimms.ridebus.network.ProgressListener
import org.xtimms.ridebus.network.await
import org.xtimms.ridebus.network.newCallWithProgress
import org.xtimms.ridebus.util.lang.withIOContext
import org.xtimms.ridebus.util.storage.saveTo
import org.xtimms.ridebus.util.system.acquireWakeLock
import org.xtimms.ridebus.util.system.isServiceRunning
import uy.kohesive.injekt.injectLazy
import java.io.File

class DatabaseUpdateService : Service() {

    private val network: NetworkHelper by injectLazy()
    private val preferences: PreferencesHelper by injectLazy()
    private val database: RideBusDatabase by injectLazy()

    private lateinit var wakeLock: PowerManager.WakeLock
    private lateinit var notifier: DatabaseUpdateNotifier

    private val job = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.IO + job)

    override fun onCreate() {
        notifier = DatabaseUpdateNotifier(this)
        wakeLock = acquireWakeLock(javaClass.name)

        startForeground(Notifications.ID_DATABASE_UPDATER, notifier.onDownloadStarted().build())
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) return START_NOT_STICKY

        val url = intent.getStringExtra(EXTRA_DOWNLOAD_URL) ?: return START_NOT_STICKY
        val title = intent.getStringExtra(EXTRA_DOWNLOAD_TITLE) ?: getString(R.string.app_name)
        val version = intent.getStringExtra(EXTRA_DOWNLOAD_VERSION) ?: BuildConfig.DATABASE_VERSION

        serviceScope.launch {
            downloadDatabase(title, url, version)
        }

        job.invokeOnCompletion { stopSelf(startId) }
        return START_NOT_STICKY
    }

    override fun stopService(name: Intent?): Boolean {
        destroyJob()
        return super.stopService(name)
    }

    override fun onDestroy() {
        destroyJob()
    }

    private fun destroyJob() {
        serviceScope.cancel()
        job.cancel()
        if (wakeLock.isHeld) {
            wakeLock.release()
        }
    }

    private suspend fun downloadDatabase(title: String, url: String, version: String) {
        notifier.onDownloadStarted()

        val progressListener = object : ProgressListener {
            var savedProgress = 0
            var lastTick = 0L

            override fun update(bytesRead: Long, contentLength: Long, done: Boolean) {
                val progress = (100 * (bytesRead.toFloat() / contentLength)).toInt()
                val currentTime = System.currentTimeMillis()
                if (progress > savedProgress && currentTime - 200 > lastTick) {
                    savedProgress = progress
                    lastTick = currentTime
                    notifier.onProgressChange(progress)
                }
            }
        }

        try {
            val response = network.client.newCallWithProgress(GET(url), progressListener).await()

            val databasePath = database.openHelper.writableDatabase.path.replace("ridebus.db", "")
            val oldDatabaseFile = withIOContext { File(databasePath, "ridebus.db") }
            val databaseFile = withIOContext { File(databasePath, "update.db") }

            if (response.isSuccessful) {
                if (database.isOpen) {
                    database.close()
                }
                oldDatabaseFile.delete()
                response.body.source().saveTo(databaseFile)
                databaseFile.renameTo(oldDatabaseFile)
                database.openHelper.writableDatabase.beginTransaction()
                preferences.databaseVersion().set(version)
                database.openHelper.writableDatabase.setTransactionSuccessful()
            } else {
                response.close()
                throw Exception("Unsuccessful response")
            }
            notifier.onDownloadFinished(title)
        } catch (e: Exception) {
            val shouldCancel = e is CancellationException ||
                (e is StreamResetException && e.errorCode == ErrorCode.CANCEL)
            if (shouldCancel) {
                notifier.cancel()
            } else {
                notifier.onDownloadError(url, version)
            }
        } finally {
            database.openHelper.writableDatabase.endTransaction()
        }
    }

    companion object {

        internal const val EXTRA_DOWNLOAD_URL =
            "${BuildConfig.APPLICATION_ID}.DatabaseUpdaterService.DOWNLOAD_URL"
        internal const val EXTRA_DOWNLOAD_TITLE =
            "${BuildConfig.APPLICATION_ID}.DatabaseUpdaterService.DOWNLOAD_TITLE"
        internal const val EXTRA_DOWNLOAD_VERSION =
            "${BuildConfig.APPLICATION_ID}.DatabaseUpdaterService.DOWNLOAD_VERSION"

        private fun isRunning(context: Context): Boolean =
            context.isServiceRunning(DatabaseUpdateService::class.java)

        fun start(
            context: Context,
            url: String,
            title: String = context.getString(R.string.app_name),
            version: String
        ) {
            if (!isRunning(context)) {
                val intent = Intent(context, DatabaseUpdateService::class.java).apply {
                    putExtra(EXTRA_DOWNLOAD_TITLE, title)
                    putExtra(EXTRA_DOWNLOAD_URL, url)
                    putExtra(EXTRA_DOWNLOAD_VERSION, version)
                }
                ContextCompat.startForegroundService(context, intent)
            }
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, DatabaseUpdateService::class.java))
        }

        internal fun downloadDatabasePendingService(
            context: Context,
            url: String,
            version: String
        ): PendingIntent {
            val intent = Intent(context, DatabaseUpdateService::class.java).apply {
                putExtra(EXTRA_DOWNLOAD_URL, url)
                putExtra(EXTRA_DOWNLOAD_VERSION, version)
            }
            return PendingIntent.getService(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
    }
}
