package org.xtimms.ridebus.data.preference

import android.content.Context
import androidx.preference.PreferenceManager
import com.tfcporciuncula.flow.FlowSharedPreferences
import com.tfcporciuncula.flow.Preference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import org.xtimms.ridebus.data.preference.PreferenceValues.ThemeMode.*
import org.xtimms.ridebus.util.system.isTablet
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import org.xtimms.ridebus.data.preference.PreferenceKeys as Keys
import org.xtimms.ridebus.data.preference.PreferenceValues as Values

fun <T> Preference<T>.asImmediateFlow(block: (T) -> Unit): Flow<T> {
    block(get())
    return asFlow()
        .onEach { block(it) }
}

class PreferencesHelper(val context: Context) {

    private val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    private val flowPrefs = FlowSharedPreferences(prefs)

    fun confirmExit() = prefs.getBoolean(Keys.confirmExit, false)

    fun startScreen() = prefs.getInt(Keys.startScreen, 1)

    fun hideBottomBarOnScroll() = flowPrefs.getBoolean(Keys.hideBottomBarOnScroll, true)

    fun sideNavIconAlignment() = flowPrefs.getInt(Keys.sideNavIconAlignment, 0)

    fun themeMode() = flowPrefs.getEnum(Keys.themeMode, system)

    fun appTheme() = flowPrefs.getEnum(Keys.appTheme, Values.AppTheme.DEFAULT)

    fun tabletUiMode() = flowPrefs.getEnum(
        Keys.tabletUiMode,
        if (context.applicationContext.isTablet()) Values.TabletUiMode.ALWAYS else Values.TabletUiMode.NEVER
    )

    fun city() = flowPrefs.getEnum(Keys.city, Values.City.POLOTSK)

    fun themeDarkAmoled() = flowPrefs.getBoolean(Keys.themeDarkAmoled, false)

    fun lastSearchQuerySearchSettings() = flowPrefs.getString("last_search_query", "")

    fun dateFormat(format: String = flowPrefs.getString(Keys.dateFormat, "").get()): DateFormat =
        when (format) {
            "" -> DateFormat.getDateInstance(DateFormat.SHORT)
            else -> SimpleDateFormat(format, Locale.getDefault())
        }

    fun lang() = flowPrefs.getString(Keys.lang, "")

    fun autoUpdateSchedule() = flowPrefs.getBoolean(Keys.autoUpdateSchedule, true)

    fun reducedMotion() = prefs.getBoolean(Keys.reducedMotion, false)
}
