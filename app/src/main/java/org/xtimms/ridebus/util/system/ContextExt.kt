package org.xtimms.ridebus.util.system

import android.app.ActivityManager
import android.app.NotificationManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.util.TypedValue
import android.view.Display
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.core.graphics.alpha
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.core.net.toUri
import logcat.LogPriority
import org.xtimms.ridebus.R
import org.xtimms.ridebus.data.preference.PreferenceValues
import org.xtimms.ridebus.data.preference.PreferencesHelper
import org.xtimms.ridebus.util.lang.truncateCenter
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get
import java.io.File
import kotlin.math.roundToInt

private const val TABLET_UI_MIN_SCREEN_WIDTH_DP = 720

/**
 * Display a toast in this context.
 *
 * @param resource the text resource.
 * @param duration the duration of the toast. Defaults to short.
 */
fun Context.toast(
    @StringRes resource: Int,
    duration: Int = Toast.LENGTH_SHORT,
    block: (Toast) -> Unit = {}
): Toast {
    return toast(getString(resource), duration, block)
}

/**
 * Display a toast in this context.
 *
 * @param text the text to display.
 * @param duration the duration of the toast. Defaults to short.
 */
fun Context.toast(
    text: String?,
    duration: Int = Toast.LENGTH_SHORT,
    block: (Toast) -> Unit = {
    }
): Toast {
    return Toast.makeText(this, text.orEmpty(), duration).also {
        block(it)
        it.show()
    }
}

fun Context.copyToClipboard(label: String, content: String) {
    if (content.isBlank()) return

    try {
        val clipboard = checkNotNull(getSystemService<ClipboardManager>())
        clipboard.setPrimaryClip(ClipData.newPlainText(label, content))

        toast(getString(R.string.copied_to_clipboard, content.truncateCenter(50)))
    } catch (e: Throwable) {
        logcat(LogPriority.ERROR, e)
        toast(R.string.clipboard_copy_error)
    }
}

/**
 * Opens a URL in a custom tab.
 */
fun Context.openInBrowser(url: String, @ColorInt toolbarColor: Int? = null) {
    this.openInBrowser(url.toUri(), toolbarColor)
}

fun Context.openInBrowser(uri: Uri, @ColorInt toolbarColor: Int? = null) {
    try {
        val intent = CustomTabsIntent.Builder()
            .setDefaultColorSchemeParams(
                CustomTabColorSchemeParams.Builder()
                    .setToolbarColor(
                        toolbarColor ?: getResourceColor(androidx.appcompat.R.attr.colorPrimary)
                    )
                    .build()
            )
            .build()
        intent.launchUrl(this, uri)
    } catch (e: Exception) {
        toast(e.message)
    }
}

fun Context.notificationBuilder(
    channelId: String,
    block: (NotificationCompat.Builder.() -> Unit)? = null
): NotificationCompat.Builder {
    val builder = NotificationCompat.Builder(this, channelId)
    if (block != null) {
        builder.block()
    }
    return builder
}

@ColorInt
fun Context.getResourceColor(@AttrRes resource: Int, alphaFactor: Float = 1f): Int {
    val typedArray = obtainStyledAttributes(intArrayOf(resource))
    val color = typedArray.getColor(0, 0)
    typedArray.recycle()

    if (alphaFactor < 1f) {
        val alpha = (color.alpha * alphaFactor).roundToInt()
        return Color.argb(alpha, color.red, color.green, color.blue)
    }

    return color
}

val Float.dpToPxEnd: Float
    get() = (
        this * Resources.getSystem().displayMetrics.density *
            if (Resources.getSystem().isLTR) 1 else -1
        )

/**
 * Converts to px.
 */
val Int.dpToPx: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

val Context.notificationManager: NotificationManager
    get() = checkNotNull(getSystemService())

val Context.powerManager: PowerManager
    get() = checkNotNull(getSystemService())

val Context.inputMethodManager: InputMethodManager
    get() = checkNotNull(getSystemService())

val Resources.isLTR
    get() = configuration.layoutDirection == View.LAYOUT_DIRECTION_LTR

fun Context.createFileInCacheDir(name: String): File {
    val file = File(externalCacheDir, name)
    if (file.exists()) {
        file.delete()
    }
    file.createNewFile()
    return file
}

@ColorInt fun Context.getThemeColor(attr: Int): Int {
    val tv = TypedValue()
    return if (this.theme.resolveAttribute(attr, tv, true)) {
        if (tv.resourceId != 0) {
            ContextCompat.getColor(this, tv.resourceId)
        } else {
            tv.data
        }
    } else {
        0
    }
}

/**
 * We consider anything with a width of >= 720dp as a tablet, i.e. with layouts in layout-sw720dp.
 */
fun Context.isTablet(): Boolean {
    return resources.configuration.smallestScreenWidthDp >= TABLET_UI_MIN_SCREEN_WIDTH_DP
}

fun Context.prepareTabletUiContext(): Context {
    val configuration = resources.configuration
    val expected = when (Injekt.get<PreferencesHelper>().tabletUiMode().get()) {
        PreferenceValues.TabletUiMode.ALWAYS -> true
        PreferenceValues.TabletUiMode.LANDSCAPE -> configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        PreferenceValues.TabletUiMode.NEVER -> false
    }
    if (configuration.smallestScreenWidthDp >= TABLET_UI_MIN_SCREEN_WIDTH_DP != expected) {
        val overrideConf = Configuration()
        overrideConf.setTo(configuration)
        overrideConf.smallestScreenWidthDp = if (expected) {
            overrideConf.smallestScreenWidthDp.coerceAtLeast(TABLET_UI_MIN_SCREEN_WIDTH_DP)
        } else {
            overrideConf.smallestScreenWidthDp.coerceAtMost(TABLET_UI_MIN_SCREEN_WIDTH_DP - 1)
        }
        return createConfigurationContext(overrideConf)
    }
    return this
}

/** Gets the duration multiplier for general animations on the device
 * @see Settings.Global.ANIMATOR_DURATION_SCALE
 */
val Context.animatorDurationScale: Float
    get() = Settings.Global.getFloat(
        this.contentResolver,
        Settings.Global.ANIMATOR_DURATION_SCALE,
        1f
    )

/**
 * Convenience method to acquire a partial wake lock.
 */
fun Context.acquireWakeLock(tag: String): PowerManager.WakeLock {
    val wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "$tag:WakeLock")
    wakeLock.acquire()
    return wakeLock
}

/**
 * Returns true if the given service class is running.
 */
fun Context.isServiceRunning(serviceClass: Class<*>): Boolean {
    val className = serviceClass.name
    val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    @Suppress("DEPRECATION")
    return manager.getRunningServices(Integer.MAX_VALUE)
        .any { className == it.service.className }
}

fun Context.defaultBrowserPackageName(): String? {
    val browserIntent = Intent(Intent.ACTION_VIEW, "http://".toUri())
    return packageManager.resolveActivity(browserIntent, PackageManager.MATCH_DEFAULT_ONLY)
        ?.activityInfo?.packageName
        ?.takeUnless { it in DeviceUtil.invalidDefaultBrowsers }
}

val Context.displayCompat: Display?
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        display
    } else {
        @Suppress("DEPRECATION")
        getSystemService<WindowManager>()?.defaultDisplay
    }

fun Context.isNightMode(): Boolean {
    return resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
}
