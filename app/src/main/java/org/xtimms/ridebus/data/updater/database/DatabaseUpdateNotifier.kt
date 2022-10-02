package org.xtimms.ridebus.data.updater.database

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import org.xtimms.ridebus.R
import org.xtimms.ridebus.data.notification.NotificationReceiver
import org.xtimms.ridebus.data.notification.Notifications
import org.xtimms.ridebus.util.system.notificationBuilder
import org.xtimms.ridebus.util.system.notificationManager

class DatabaseUpdateNotifier(private val context: Context) {

    private val notificationBuilder = context.notificationBuilder(Notifications.CHANNEL_DATABASE_UPDATE)

    private fun NotificationCompat.Builder.show(id: Int = Notifications.ID_DATABASE_UPDATER) {
        context.notificationManager.notify(id, build())
    }

    @SuppressLint("LaunchActivityFromNotification")
    fun promptUpdate(release: GithubDatabase) {
        val intent = Intent(context, DatabaseUpdateService::class.java).apply {
            putExtra(DatabaseUpdateService.EXTRA_DOWNLOAD_URL, release.getDownloadLink())
            putExtra(DatabaseUpdateService.EXTRA_DOWNLOAD_VERSION, release.version)
        }
        val updateIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        with(notificationBuilder) {
            setContentTitle(context.getString(R.string.update_check_notification_database_update_available))
            setContentText(context.getString(R.string.new_version_s, release.version))
            setSmallIcon(R.drawable.ic_ridebus)
            setContentIntent(updateIntent)

            clearActions()
            addAction(
                android.R.drawable.stat_sys_download_done,
                context.getString(R.string.action_download),
                updateIntent,
            )
        }
        notificationBuilder.show()
    }

    fun onDownloadStarted(title: String? = null): NotificationCompat.Builder {
        with(notificationBuilder) {
            title?.let { setContentTitle(title) }
            setContentText(context.getString(R.string.update_check_notification_download_in_progress))
            setSmallIcon(android.R.drawable.stat_sys_download)
            setOngoing(true)
        }
        notificationBuilder.show()
        return notificationBuilder
    }

    fun onProgressChange(progress: Int) {
        with(notificationBuilder) {
            setProgress(100, progress, false)
            setOnlyAlertOnce(true)
        }
        notificationBuilder.show()
    }

    fun onDownloadFinished() {
        with(notificationBuilder) {
            setContentText(context.getString(R.string.update_check_notification_download_database_complete))
            setSmallIcon(android.R.drawable.stat_sys_download_done)
            setOnlyAlertOnce(false)
            setProgress(0, 0, false)
            clearActions()
            addAction(
                R.drawable.ic_close,
                context.getString(R.string.action_cancel),
                NotificationReceiver.dismissNotificationPendingBroadcast(context, Notifications.ID_DATABASE_UPDATER),
            )
        }
        notificationBuilder.show()
    }

    fun onDownloadError(url: String, version: String) {
        with(notificationBuilder) {
            setContentText(context.getString(R.string.update_check_notification_download_error))
            setSmallIcon(R.drawable.ic_alert)
            setOnlyAlertOnce(false)
            setProgress(0, 0, false)

            clearActions()
            addAction(
                R.drawable.ic_refresh,
                context.getString(R.string.action_retry),
                DatabaseUpdateService.downloadDatabasePendingService(context, url, version),
            )
            addAction(
                R.drawable.ic_close,
                context.getString(R.string.action_cancel),
                NotificationReceiver.dismissNotificationPendingBroadcast(context, Notifications.ID_DATABASE_UPDATER),
            )
        }
        notificationBuilder.show(Notifications.ID_DATABASE_UPDATER)
    }
}
