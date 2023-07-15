package org.xtimms.ridebus.data.updater.database

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import org.xtimms.ridebus.R
import org.xtimms.ridebus.data.notification.NotificationReceiver
import org.xtimms.ridebus.data.notification.Notifications
import org.xtimms.ridebus.ui.main.MainActivity
import org.xtimms.ridebus.util.system.notificationBuilder
import org.xtimms.ridebus.util.system.notificationManager

class DatabaseUpdateNotifier(private val context: Context) {

    private val notificationBuilder = context.notificationBuilder(Notifications.CHANNEL_DATABASE_UPDATE)

    private fun NotificationCompat.Builder.show(id: Int = Notifications.ID_DATABASE_UPDATER) {
        context.notificationManager.notify(id, build())
    }

    fun cancel() {
        NotificationReceiver.dismissNotification(context, Notifications.ID_APP_UPDATER)
    }

    @SuppressLint("LaunchActivityFromNotification")
    fun promptUpdate(release: GithubDatabase) {
        val intent = Intent(context, DatabaseUpdateService::class.java).apply {
            putExtra(DatabaseUpdateService.EXTRA_DOWNLOAD_URL, release.getDownloadLink())
            putExtra(DatabaseUpdateService.EXTRA_DOWNLOAD_TITLE, release.info)
            putExtra(DatabaseUpdateService.EXTRA_DOWNLOAD_VERSION, release.version)
        }
        val updateIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        with(notificationBuilder) {
            setContentTitle(context.getString(R.string.update_check_notification_database_update_available))
            setContentText(context.getString(R.string.new_version_s, release.version))
            setSmallIcon(R.drawable.ic_ridebus)
            setContentIntent(updateIntent)
            clearActions()
            addAction(
                android.R.drawable.stat_sys_download_done,
                context.getString(R.string.action_download),
                updateIntent
            )
            addAction(
                R.drawable.ic_close,
                context.getString(R.string.action_postpone),
                NotificationReceiver.dismissNotificationPendingBroadcast(context, Notifications.ID_DATABASE_UPDATER)
            )
        }
        notificationBuilder.show()
    }

    @SuppressLint("LaunchActivityFromNotification")
    fun promptCriticalUpdate(release: GithubDatabase) {
        val intent = Intent(context, DatabaseUpdateService::class.java).apply {
            putExtra(DatabaseUpdateService.EXTRA_DOWNLOAD_URL, release.getDownloadLink())
            putExtra(DatabaseUpdateService.EXTRA_DOWNLOAD_TITLE, release.info)
            putExtra(DatabaseUpdateService.EXTRA_DOWNLOAD_VERSION, release.version)
        }
        val updateIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        with(notificationBuilder) {
            setContentTitle(context.getString(R.string.update_check_notification_database_update_available))
            setContentText(context.getString(R.string.new_version_s, release.version))
            setSmallIcon(R.drawable.ic_ridebus)
            setContentIntent(updateIntent)
            setOngoing(true)
            clearActions()
            addAction(
                android.R.drawable.stat_sys_download_done,
                context.getString(R.string.action_download),
                updateIntent
            )
        }
        notificationBuilder.show()
    }

    fun onDownloadStarted(): NotificationCompat.Builder {
        with(notificationBuilder) {
            setContentTitle(context.getString(R.string.app_name))
            setContentText(context.getString(R.string.update_check_notification_download_in_progress))
            setSmallIcon(android.R.drawable.stat_sys_download)
            setOngoing(true)

            clearActions()
            addAction(
                R.drawable.ic_close,
                context.getString(R.string.action_cancel),
                NotificationReceiver.cancelUpdateDownloadPendingBroadcast(context)
            )
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

    fun onDownloadFinished(info: String? = null) {
        val resultIntent = Intent(context, MainActivity::class.java)
        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(resultIntent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        val bigTextStyle = NotificationCompat.BigTextStyle()
            .bigText(info)
            .setBigContentTitle(context.getString(R.string.update_check_notification_download_database_complete))

        with(notificationBuilder) {
            setStyle(bigTextStyle)
            setContentTitle(context.getString(R.string.update_check_notification_download_database_complete))
            info?.let { setContentText(info) }
            setSmallIcon(R.drawable.ic_done_all)
            setContentIntent(resultPendingIntent)
            setOnlyAlertOnce(false)
            setProgress(0, 0, false)
            setOngoing(false)
            clearActions()
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
                DatabaseUpdateService.downloadDatabasePendingService(context, url, version)
            )
            addAction(
                R.drawable.ic_close,
                context.getString(R.string.action_cancel),
                NotificationReceiver.dismissNotificationPendingBroadcast(context, Notifications.ID_DATABASE_UPDATER)
            )
        }
        notificationBuilder.show(Notifications.ID_DATABASE_UPDATER)
    }
}
