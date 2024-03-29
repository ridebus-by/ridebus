package org.xtimms.ridebus.data.notification

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.NotificationManagerCompat.IMPORTANCE_DEFAULT
import androidx.core.app.NotificationManagerCompat.IMPORTANCE_HIGH
import org.xtimms.ridebus.R
import org.xtimms.ridebus.util.system.buildNotificationChannel

object Notifications {

    /**
     * Notification channel used for app and database updates.
     */
    const val CHANNEL_APP_UPDATE = "app_apk_update_channel"
    const val ID_APP_UPDATER = 1
    const val CHANNEL_DATABASE_UPDATE = "database_update_channel"
    const val ID_DATABASE_UPDATER = 2

    private val deprecatedChannels = listOf(
        "crash_logs_channel"
    )

    fun createChannels(context: Context) {
        val notificationService = NotificationManagerCompat.from(context)

        deprecatedChannels.forEach(notificationService::deleteNotificationChannel)

        notificationService.createNotificationChannelsCompat(
            listOf(
                buildNotificationChannel(CHANNEL_APP_UPDATE, IMPORTANCE_DEFAULT) {
                    setName(context.getString(R.string.channel_app_updates))
                },
                buildNotificationChannel(CHANNEL_DATABASE_UPDATE, IMPORTANCE_HIGH) {
                    setName(context.getString(R.string.channel_databases_updates))
                }
            )
        )
    }
}
