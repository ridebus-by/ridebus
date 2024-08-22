package org.xtimms.ridebus.data.notification

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import org.xtimms.ridebus.data.updater.AppUpdateService
import org.xtimms.ridebus.util.system.notificationManager
import org.xtimms.ridebus.BuildConfig.APPLICATION_ID as ID

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            // Dismiss notification
            ACTION_DISMISS_NOTIFICATION -> dismissNotification(context, intent.getIntExtra(EXTRA_NOTIFICATION_ID, -1))
            // Cancel downloading app update
            ACTION_CANCEL_APP_UPDATE_DOWNLOAD -> cancelDownloadAppUpdate(context)
        }
    }

    private fun cancelDownloadAppUpdate(context: Context) {
        AppUpdateService.stop(context)
    }

    companion object {
        private const val NAME = "NotificationReceiver"

        private const val ACTION_DISMISS_NOTIFICATION = "$ID.$NAME.ACTION_DISMISS_NOTIFICATION"

        private const val EXTRA_NOTIFICATION_ID = "$ID.$NAME.NOTIFICATION_ID"

        private const val ACTION_CANCEL_APP_UPDATE_DOWNLOAD = "$ID.$NAME.CANCEL_APP_UPDATE_DOWNLOAD"

        /**
         * Returns [PendingIntent] that starts a service which dismissed the notification
         *
         * @param context context of application
         * @param notificationId id of notification
         * @return [PendingIntent]
         */
        internal fun dismissNotificationPendingBroadcast(context: Context, notificationId: Int): PendingIntent {
            val intent = Intent(context, NotificationReceiver::class.java).apply {
                action = ACTION_DISMISS_NOTIFICATION
                putExtra(EXTRA_NOTIFICATION_ID, notificationId)
            }
            return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        }

        /**
         * Returns [PendingIntent] that starts a service which dismissed the notification
         *
         * @param context context of application
         * @param notificationId id of notification
         * @return [PendingIntent]
         */
        internal fun dismissNotification(context: Context, notificationId: Int, groupId: Int? = null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val groupKey = context.notificationManager.activeNotifications.find {
                    it.id == notificationId
                }?.groupKey

                if (groupId != null && groupId != 0 && groupKey != null && groupKey.isNotEmpty()) {
                    val notifications = context.notificationManager.activeNotifications.filter {
                        it.groupKey == groupKey
                    }

                    if (notifications.size == 2) {
                        context.notificationManager.cancel(groupId)
                        return
                    }
                }
            }

            context.notificationManager.cancel(notificationId)
        }

        internal fun cancelUpdateDownloadPendingBroadcast(context: Context): PendingIntent {
            val intent = Intent(context, NotificationReceiver::class.java).apply {
                action = ACTION_CANCEL_APP_UPDATE_DOWNLOAD
            }
            return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }
    }
}
