package org.xtimms.ridebus.util

import android.content.Context
import android.os.Build
import org.xtimms.ridebus.BuildConfig
import org.xtimms.ridebus.util.lang.withNonCancellableContext
import org.xtimms.ridebus.util.lang.withUIContext
import org.xtimms.ridebus.util.storage.getUriCompat
import org.xtimms.ridebus.util.system.createFileInCacheDir
import org.xtimms.ridebus.util.system.toShareIntent
import org.xtimms.ridebus.util.system.toast

class CrashLogUtil(private val context: Context) {

    suspend fun dumpLogs() = withNonCancellableContext {
        try {
            val file = context.createFileInCacheDir("ridebus_crash_logs.txt")
            Runtime.getRuntime().exec("logcat *:E -d -f ${file.absolutePath}").waitFor()
            file.appendText(getDebugInfo())

            val uri = file.getUriCompat(context)
            context.startActivity(uri.toShareIntent(context, "text/plain"))
        } catch (e: Throwable) {
            withUIContext { context.toast("Failed to get logs") }
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
}
