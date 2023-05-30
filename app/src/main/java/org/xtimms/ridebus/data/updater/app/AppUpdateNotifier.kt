package org.xtimms.ridebus.data.updater.app

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import org.xtimms.ridebus.R
import org.xtimms.ridebus.data.notification.NotificationHandler
import org.xtimms.ridebus.data.notification.NotificationReceiver
import org.xtimms.ridebus.data.notification.Notifications
import org.xtimms.ridebus.util.system.notificationBuilder
import org.xtimms.ridebus.util.system.notificationManager

internal class AppUpdateNotifier(private val context: Context) {

    private val notificationBuilder = context.notificationBuilder(Notifications.CHANNEL_APP_UPDATE)

    private fun NotificationCompat.Builder.show(id: Int = Notifications.ID_APP_UPDATER) {
        context.notificationManager.notify(id, build())
    }

    fun cancel() {
        NotificationReceiver.dismissNotification(context, Notifications.ID_APP_UPDATER)
    }

    fun promptUpdate(release: GithubRelease) {
        val intent = Intent(context, AppUpdateService::class.java).apply {
            putExtra(AppUpdateService.EXTRA_DOWNLOAD_URL, release.getDownloadLink())
        }
        val updateIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val releaseIntent = Intent(Intent.ACTION_VIEW, release.releaseLink.toUri()).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val releaseInfoIntent = PendingIntent.getActivity(context, release.hashCode(), releaseIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        with(notificationBuilder) {
            setContentTitle(context.getString(R.string.update_check_notification_update_available))
            setContentText(context.getString(R.string.new_version_s, release.version))
            setSmallIcon(android.R.drawable.stat_sys_download_done)
            setContentIntent(updateIntent)

            clearActions()
            addAction(
                android.R.drawable.stat_sys_download_done,
                context.getString(R.string.action_download),
                updateIntent
            )
            addAction(
                R.drawable.ic_info,
                context.getString(R.string.whats_new),
                releaseInfoIntent
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

            clearActions()
            addAction(
                R.drawable.ic_close,
                context.getString(R.string.action_cancel),
                NotificationReceiver.cancelUpdateDownloadPendingBroadcast(context),
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

    fun onDownloadFinished(uri: Uri) {
        val installIntent = NotificationHandler.installApkPendingActivity(context, uri)
        with(notificationBuilder) {
            setContentText(context.getString(R.string.update_check_notification_download_complete))
            setSmallIcon(android.R.drawable.stat_sys_download_done)
            setOnlyAlertOnce(false)
            setProgress(0, 0, false)
            setContentIntent(installIntent)
            setOngoing(true)

            clearActions()
            addAction(
                R.drawable.ic_system_update_alt,
                context.getString(R.string.action_install),
                installIntent
            )
            addAction(
                R.drawable.ic_close,
                context.getString(R.string.action_cancel),
                NotificationReceiver.dismissNotificationPendingBroadcast(context, Notifications.ID_APP_UPDATER)
            )
        }
        notificationBuilder.show()
    }

    fun onDownloadError(url: String) {
        with(notificationBuilder) {
            setContentText(context.getString(R.string.update_check_notification_download_error))
            setSmallIcon(R.drawable.ic_alert)
            setOnlyAlertOnce(false)
            setProgress(0, 0, false)

            clearActions()
            addAction(
                R.drawable.ic_refresh,
                context.getString(R.string.action_retry),
                AppUpdateService.downloadApkPendingService(context, url)
            )
            addAction(
                R.drawable.ic_close,
                context.getString(R.string.action_cancel),
                NotificationReceiver.dismissNotificationPendingBroadcast(context, Notifications.ID_APP_UPDATER)
            )
        }
        notificationBuilder.show(Notifications.ID_APP_UPDATER)
    }
}
