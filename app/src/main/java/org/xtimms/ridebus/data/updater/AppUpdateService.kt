package org.xtimms.ridebus.data.updater

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
import org.xtimms.ridebus.data.notification.Notifications
import org.xtimms.ridebus.network.GET
import org.xtimms.ridebus.network.NetworkHelper
import org.xtimms.ridebus.network.ProgressListener
import org.xtimms.ridebus.network.await
import org.xtimms.ridebus.network.newCallWithProgress
import org.xtimms.ridebus.util.storage.getUriCompat
import org.xtimms.ridebus.util.storage.saveTo
import org.xtimms.ridebus.util.system.acquireWakeLock
import org.xtimms.ridebus.util.system.isServiceRunning
import uy.kohesive.injekt.injectLazy
import java.io.File

class AppUpdateService : Service() {

    private val network: NetworkHelper by injectLazy()

    /**
     * Wake lock that will be held until the service is destroyed.
     */
    private lateinit var wakeLock: PowerManager.WakeLock
    private lateinit var notifier: AppUpdateNotifier

    private val job = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.IO + job)

    override fun onCreate() {
        notifier = AppUpdateNotifier(this)
        wakeLock = acquireWakeLock(javaClass.name)

        startForeground(Notifications.ID_APP_UPDATER, notifier.onDownloadStarted().build())
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) return START_NOT_STICKY

        val url = intent.getStringExtra(EXTRA_DOWNLOAD_URL) ?: return START_NOT_STICKY
        val title = intent.getStringExtra(EXTRA_DOWNLOAD_TITLE) ?: getString(R.string.app_name)

        serviceScope.launch {
            downloadApk(title, url)
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

    private suspend fun downloadApk(title: String, url: String) {
        notifier.onDownloadStarted(title)

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
            val apkFile = File(externalCacheDir, "update.apk")

            if (response.isSuccessful) {
                response.body.source().saveTo(apkFile)
            } else {
                response.close()
                throw Exception("Unsuccessful response")
            }
            notifier.onDownloadFinished(apkFile.getUriCompat(this))
        } catch (e: Exception) {
            val shouldCancel = e is CancellationException ||
                (e is StreamResetException && e.errorCode == ErrorCode.CANCEL)
            if (shouldCancel) {
                notifier.cancel()
            } else {
                notifier.onDownloadError(url)
            }
        }
    }

    companion object {

        internal const val EXTRA_DOWNLOAD_URL =
            "${BuildConfig.APPLICATION_ID}.UpdaterService.DOWNLOAD_URL"
        internal const val EXTRA_DOWNLOAD_TITLE =
            "${BuildConfig.APPLICATION_ID}.UpdaterService.DOWNLOAD_TITLE"

        private fun isRunning(context: Context): Boolean =
            context.isServiceRunning(AppUpdateService::class.java)

        fun start(
            context: Context,
            url: String,
            title: String = context.getString(R.string.app_name)
        ) {
            if (isRunning(context)) return

            Intent(context, AppUpdateService::class.java).apply {
                putExtra(EXTRA_DOWNLOAD_TITLE, title)
                putExtra(EXTRA_DOWNLOAD_URL, url)
                ContextCompat.startForegroundService(context, this)
            }
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, AppUpdateService::class.java))
        }

        internal fun downloadApkPendingService(context: Context, url: String): PendingIntent {
            val intent = Intent(context, AppUpdateService::class.java).apply {
                putExtra(EXTRA_DOWNLOAD_URL, url)
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
