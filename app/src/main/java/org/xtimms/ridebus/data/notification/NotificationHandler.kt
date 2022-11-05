package org.xtimms.ridebus.data.notification

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri

/**
 * Class that manages [PendingIntent] of activity's
 */
object NotificationHandler {

    /**
     * Returns [PendingIntent] that prompts user with apk install intent
     *
     * @param context context
     * @param uri uri of apk that is installed
     */
    fun installApkPendingActivity(context: Context, uri: Uri): PendingIntent {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.android.package-archive")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    }
}
