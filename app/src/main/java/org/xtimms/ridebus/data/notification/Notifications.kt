package org.xtimms.ridebus.data.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import org.xtimms.ridebus.R
import org.xtimms.ridebus.util.system.notificationManager

object Notifications {

    /**
     * Notification channel used for crash log file sharing.
     */
    const val CHANNEL_CRASH_LOGS = "crash_logs_channel"
    const val ID_CRASH_LOGS = -601

    fun createChannels(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        listOf(
            NotificationChannel(
                CHANNEL_CRASH_LOGS,
                context.getString(R.string.channel_crash_logs),
                NotificationManager.IMPORTANCE_HIGH
            )
        ).forEach(context.notificationManager::createNotificationChannel)

    }

}