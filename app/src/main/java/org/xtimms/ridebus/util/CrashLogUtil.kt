package org.xtimms.ridebus.util

import android.content.Context
import android.net.Uri
import android.os.Build
import org.xtimms.ridebus.BuildConfig
import org.xtimms.ridebus.R
import org.xtimms.ridebus.data.notification.NotificationReceiver
import org.xtimms.ridebus.data.notification.Notifications
import org.xtimms.ridebus.util.lang.launchIO
import org.xtimms.ridebus.util.lang.withUIContext
import org.xtimms.ridebus.util.storage.getUriCompat
import org.xtimms.ridebus.util.system.createFileInCacheDir
import org.xtimms.ridebus.util.system.notificationBuilder
import org.xtimms.ridebus.util.system.notificationManager
import org.xtimms.ridebus.util.system.toast

class CrashLogUtil(private val context: Context) {

    private val notificationBuilder = context.notificationBuilder(Notifications.CHANNEL_CRASH_LOGS) {
        setSmallIcon(R.drawable.ic_ridebus)
    }

    fun dumpLogs() {
        launchIO {
            try {
                val file = context.createFileInCacheDir("ridebus_crash_logs.txt")
                Runtime.getRuntime().exec("logcat *:E -d -f ${file.absolutePath}").waitFor()
                file.appendText(getDebugInfo())

                showNotification(file.getUriCompat(context))
            } catch (e: Throwable) {
                withUIContext { context.toast("Failed to get logs") }
            }
        }
    }

    fun getDebugInfo(): String {
        return """
            App version: ${BuildConfig.VERSION_NAME} (${BuildConfig.COMMIT_SHA}, ${BuildConfig.VERSION_CODE})
            Android version: ${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})
            Android build ID: ${Build.DISPLAY}
            Device brand: ${Build.BRAND}
            Device manufacturer: ${Build.MANUFACTURER}
            Device name: ${Build.DEVICE}
            Device model: ${Build.MODEL}
            Device product name: ${Build.PRODUCT}
        """.trimIndent()
    }

    private fun showNotification(uri: Uri) {
        context.notificationManager.cancel(Notifications.ID_CRASH_LOGS)

        with(notificationBuilder) {
            setContentTitle(context.getString(R.string.crash_log_saved))

            clearActions()
            addAction(
                R.drawable.ic_folder,
                context.getString(R.string.action_open_log),
                NotificationReceiver.openErrorLogPendingActivity(context, uri)
            )
            addAction(
                R.drawable.ic_share,
                context.getString(R.string.action_share),
                NotificationReceiver.shareCrashLogPendingBroadcast(context, uri, Notifications.ID_CRASH_LOGS)
            )

            context.notificationManager.notify(Notifications.ID_CRASH_LOGS, build())
        }
    }
}