package org.xtimms.ridebus.data.preference

import android.content.Context
import android.os.Build
import android.os.Environment
import androidx.core.net.toUri
import androidx.preference.PreferenceManager
import com.fredporciuncula.flow.preferences.FlowSharedPreferences
import org.xtimms.ridebus.BuildConfig
import org.xtimms.ridebus.R
import org.xtimms.ridebus.data.preference.PreferenceValues.AppTheme.DEFAULT
import org.xtimms.ridebus.data.preference.PreferenceValues.AppTheme.MONET
import org.xtimms.ridebus.data.preference.PreferenceValues.TabletUiMode.ALWAYS
import org.xtimms.ridebus.data.preference.PreferenceValues.TabletUiMode.NEVER
import org.xtimms.ridebus.data.preference.PreferenceValues.ThemeMode.light
import org.xtimms.ridebus.data.preference.PreferenceValues.ThemeMode.system
import org.xtimms.ridebus.util.system.DeviceUtil
import org.xtimms.ridebus.util.system.isTablet
import java.io.File
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import org.xtimms.ridebus.data.preference.PreferenceKeys as Keys

class PreferencesHelper(val context: Context) {

    private val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    private val flowPrefs = FlowSharedPreferences(prefs)

    private val defaultBackupDir = File(
        Environment.getExternalStorageDirectory().absolutePath + File.separator +
            context.getString(R.string.app_name),
        "backup"
    ).toUri()

    fun confirmExit() = prefs.getBoolean(Keys.confirmExit, false)

    fun startScreen() = prefs.getInt(Keys.startScreen, 1)

    fun hideBottomBarOnScroll() = flowPrefs.getBoolean("pref_hide_bottom_bar_on_scroll", true)

    fun sideNavIconAlignment() = flowPrefs.getInt("pref_side_nav_icon_alignment", 0)

    fun sideNavLabels() = flowPrefs.getInt("pref_side_nav_labels", 0)

    fun themeMode() = flowPrefs.getEnum(
        "pref_theme_mode_key",
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { system } else { light }
    )

    fun appTheme() = flowPrefs.getEnum(
        "pref_app_theme",
        if (DeviceUtil.isDynamicColorAvailable) { MONET } else { DEFAULT }
    )

    fun tabletUiMode() = flowPrefs.getEnum(
        "tablet_ui_mode",
        if (context.applicationContext.isTablet()) ALWAYS else NEVER
    )

    fun city() = flowPrefs.getString(Keys.city, "0")

    fun themeDarkAmoled() = flowPrefs.getBoolean("pref_theme_dark_amoled_key", false)

    fun lastSearchQuerySearchSettings() = flowPrefs.getString("last_search_query", "")

    fun dateFormat(format: String = flowPrefs.getString(Keys.dateFormat, "").get()): DateFormat =
        when (format) {
            "" -> DateFormat.getDateInstance(DateFormat.SHORT)
            else -> SimpleDateFormat(format, Locale.getDefault())
        }

    fun transliterate() = flowPrefs.getBoolean("transliterate", false)

    fun autoUpdateSchedule() = flowPrefs.getBoolean("auto_update_schedule", true)

    fun reducedMotion() = prefs.getBoolean(Keys.reducedMotion, false)

    fun verboseLogging() = prefs.getBoolean(Keys.verboseLogging, false)

    fun pinnedFavourites() = flowPrefs.getStringSet("pinned_favourites", emptySet())

    fun bottomBarLabels() = flowPrefs.getBoolean("pref_show_bottom_bar_labels", true)

    fun lastAppCheck() = flowPrefs.getLong("last_app_check", 0)

    fun lastVersionCode() = flowPrefs.getInt("last_version_code", 0)

    fun lastDatabaseCheck() = flowPrefs.getLong("last_database_check", 0)

    fun databaseVersion() = flowPrefs.getString("database_version", BuildConfig.DATABASE_VERSION)

    fun categoryTabs() = flowPrefs.getBoolean("display_category_tabs", true)

    fun categoryNumberOfItems() = flowPrefs.getBoolean("display_number_of_items", false)

    fun lastUsedFavouriteItem() = flowPrefs.getInt("last_favourite_item", -1)

    fun favourites() = flowPrefs.getStringSet("favourites", emptySet())

    fun enabledTypes() = flowPrefs.getStringSet("transport_types", setOf("1", "2", "3", "4"))

    fun disabledRoutes() = flowPrefs.getStringSet("hidden_routes", emptySet())

    fun isVisibleAttentionNote() = flowPrefs.getBoolean("attention_note", true)

    fun appLocale() = flowPrefs.getString("app_locale", "0")
}
