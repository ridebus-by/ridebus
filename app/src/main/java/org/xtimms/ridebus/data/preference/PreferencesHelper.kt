package org.xtimms.ridebus.data.preference

import android.content.Context
import android.content.res.Configuration
import androidx.preference.PreferenceManager
import com.tfcporciuncula.flow.FlowSharedPreferences
import com.tfcporciuncula.flow.Preference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import org.xtimms.ridebus.data.preference.PreferenceValues.ThemeMode.*
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

    fun themeMode() = flowPrefs.getEnum(Keys.themeMode, system)

    fun themeLight() = flowPrefs.getEnum(Keys.themeLight, Values.LightThemeVariant.default)

    fun themeDark() = flowPrefs.getEnum(Keys.themeDark, Values.DarkThemeVariant.default)

    fun lastSearchQuerySearchSettings() = flowPrefs.getString("last_search_query", "")

    fun hideBottomBar() = flowPrefs.getBoolean(Keys.hideBottomBar, true)

    fun dateFormat(format: String = flowPrefs.getString(Keys.dateFormat, "").get()): DateFormat =
        when (format) {
            "" -> DateFormat.getDateInstance(DateFormat.SHORT)
            else -> SimpleDateFormat(format, Locale.getDefault())
        }

    fun lang() = prefs.getString(Keys.lang, "")

    fun isDarkMode(): Boolean {
        return when (themeMode().get()) {
            light -> false
            dark -> true
            system -> {
                context.applicationContext.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK ==
                        Configuration.UI_MODE_NIGHT_YES
            }
        }
    }

}