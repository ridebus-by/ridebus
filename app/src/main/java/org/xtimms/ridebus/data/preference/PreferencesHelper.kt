package org.xtimms.ridebus.data.preference

import android.content.Context
import android.os.Build
import androidx.preference.PreferenceManager
import com.google.android.material.color.DynamicColors
import com.tfcporciuncula.flow.FlowSharedPreferences
import org.xtimms.ridebus.data.preference.PreferenceValues.AppTheme.*
import org.xtimms.ridebus.data.preference.PreferenceValues.City.*
import org.xtimms.ridebus.data.preference.PreferenceValues.TabletUiMode.*
import org.xtimms.ridebus.data.preference.PreferenceValues.ThemeMode.*
import org.xtimms.ridebus.util.system.isTablet
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import org.xtimms.ridebus.data.preference.PreferenceKeys as Keys

class PreferencesHelper(val context: Context) {

    private val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    private val flowPrefs = FlowSharedPreferences(prefs)

    fun confirmExit() = prefs.getBoolean(Keys.confirmExit, false)

    fun startScreen() = prefs.getInt(Keys.startScreen, 1)

    fun hideBottomBarOnScroll() = flowPrefs.getBoolean("pref_hide_bottom_bar_on_scroll", true)

    fun sideNavIconAlignment() = flowPrefs.getInt("pref_side_nav_icon_alignment", 0)

    fun themeMode() = flowPrefs.getEnum(
        "pref_theme_mode_key",
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { system } else { light }
    )

    fun appTheme() = flowPrefs.getEnum(
        "pref_app_theme",
        if (DynamicColors.isDynamicColorAvailable()) { MONET } else { DEFAULT }
    )

    fun tabletUiMode() = flowPrefs.getEnum(
        "tablet_ui_mode",
        if (context.applicationContext.isTablet()) ALWAYS else NEVER
    )

    fun city() = flowPrefs.getEnum("pref_city_key", POLOTSK)

    fun themeDarkAmoled() = flowPrefs.getBoolean("pref_theme_dark_amoled_key", false)

    fun lastSearchQuerySearchSettings() = flowPrefs.getString("last_search_query", "")

    fun dateFormat(format: String = flowPrefs.getString(Keys.dateFormat, "").get()): DateFormat =
        when (format) {
            "" -> DateFormat.getDateInstance(DateFormat.SHORT)
            else -> SimpleDateFormat(format, Locale.getDefault())
        }

    fun lang() = flowPrefs.getString("app_language", "")

    fun transliterate() = flowPrefs.getBoolean("transliterate", false)

    fun autoUpdateSchedule() = flowPrefs.getBoolean("auto_update_schedule", true)

    fun reducedMotion() = prefs.getBoolean(Keys.reducedMotion, false)

    fun verboseLogging() = prefs.getBoolean(Keys.verboseLogging, false)

    fun pinnedFavourites() = flowPrefs.getStringSet("pinned_favourites", emptySet())
}
