package org.xtimms.ridebus.data.updater.app

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.PowerManager
import androidx.core.content.ContextCompat
import logcat.LogPriority
import org.xtimms.ridebus.BuildConfig
import org.xtimms.ridebus.R
import org.xtimms.ridebus.data.notification.Notifications
import org.xtimms.ridebus.network.GET
import org.xtimms.ridebus.network.NetworkHelper
import org.xtimms.ridebus.network.ProgressListener
import org.xtimms.ridebus.network.await
import org.xtimms.ridebus.network.newCallWithProgress
import org.xtimms.ridebus.util.lang.launchIO
import org.xtimms.ridebus.util.storage.getUriCompat
import org.xtimms.ridebus.util.storage.saveTo
import org.xtimms.ridebus.util.system.acquireWakeLock
import org.xtimms.ridebus.util.system.isServiceRunning
import org.xtimms.ridebus.util.system.logcat
import uy.kohesive.injekt.injectLazy
import java.io.File

class AppUpdateService : Service() {

    private val network: NetworkHelper by injectLazy()

    private lateinit var wakeLock: PowerManager.WakeLock

    private lateinit var notifier: AppUpdateNotifier

    override fun onCreate() {
        super.onCreate()

        notifier = AppUpdateNotifier(this)
        wakeLock = acquireWakeLock(javaClass.name)

        startForeground(Notifications.ID_APP_UPDATER, notifier.onDownloadStarted().build())
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) return START_NOT_STICKY

        val url = intent.getStringExtra(EXTRA_DOWNLOAD_URL) ?: return START_NOT_STICKY
        val title = intent.getStringExtra(EXTRA_DOWNLOAD_TITLE) ?: getString(R.string.app_name)

        launchIO {
            downloadApk(title, url)
        }

        stopSelf(startId)
        return START_NOT_STICKY
    }

    override fun stopService(name: Intent?): Boolean {
        destroyJob()
        return super.stopService(name)
    }

    override fun onDestroy() {
        destroyJob()
        super.onDestroy()
    }

    private fun destroyJob() {
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

        val response = network.client.newCallWithProgress(GET(url), progressListener).await()

        try {
            val apkFile = File(externalCacheDir, "update.apk")

            if (response.isSuccessful) {
                response.body.source().saveTo(apkFile)
            } else {
                response.close()
                throw Exception("Unsuccessful response")
            }
            notifier.onDownloadFinished(apkFile.getUriCompat(this))
        } catch (e: Exception) {
            logcat(LogPriority.ERROR, e)
            notifier.onDownloadError(url)
        } finally {
            response.close()
        }
    }

    companion object {

        internal const val EXTRA_DOWNLOAD_URL = "${BuildConfig.APPLICATION_ID}.UpdaterService.DOWNLOAD_URL"
        internal const val EXTRA_DOWNLOAD_TITLE = "${BuildConfig.APPLICATION_ID}.UpdaterService.DOWNLOAD_TITLE"

        private fun isRunning(context: Context): Boolean =
            context.isServiceRunning(AppUpdateService::class.java)

        fun start(context: Context, url: String, title: String = context.getString(R.string.app_name)) {
            if (!isRunning(context)) {
                val intent = Intent(context, AppUpdateService::class.java).apply {
                    putExtra(EXTRA_DOWNLOAD_TITLE, title)
                    putExtra(EXTRA_DOWNLOAD_URL, url)
                }
                ContextCompat.startForegroundService(context, intent)
            }
        }

        internal fun downloadApkPendingService(context: Context, url: String): PendingIntent {
            val intent = Intent(context, AppUpdateService::class.java).apply {
                putExtra(EXTRA_DOWNLOAD_URL, url)
            }
            return PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }
}
