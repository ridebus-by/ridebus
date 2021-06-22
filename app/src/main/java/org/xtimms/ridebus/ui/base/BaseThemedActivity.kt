package org.xtimms.ridebus.ui.base

import android.content.res.Configuration.UI_MODE_NIGHT_MASK
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.xtimms.ridebus.R
import org.xtimms.ridebus.data.preference.PreferenceValues.DarkThemeVariant
import org.xtimms.ridebus.data.preference.PreferenceValues.LightThemeVariant
import org.xtimms.ridebus.data.preference.PreferenceValues.ThemeMode
import org.xtimms.ridebus.data.preference.PreferencesHelper
import uy.kohesive.injekt.injectLazy

abstract class BaseThemedActivity : AppCompatActivity() {

    val preferences: PreferencesHelper by injectLazy()

    override fun onCreate(savedInstanceState: Bundle?) {
        val isDarkMode = when (preferences.themeMode().get()) {
            ThemeMode.light -> false
            ThemeMode.dark -> true
            ThemeMode.system -> resources.configuration.uiMode and UI_MODE_NIGHT_MASK == UI_MODE_NIGHT_YES
        }
        val themeId = if (isDarkMode) {
            when (preferences.themeDark().get()) {
                DarkThemeVariant.default -> R.style.Theme_RideBus_Dark
                DarkThemeVariant.blue -> R.style.Theme_RideBus_Dark_Blue
                DarkThemeVariant.greenapple -> R.style.Theme_RideBus_Dark_GreenApple
                DarkThemeVariant.amoled -> R.style.Theme_RideBus_Amoled
            }
        } else {
            when (preferences.themeLight().get()) {
                LightThemeVariant.default -> R.style.Theme_RideBus_Light
                LightThemeVariant.blue -> R.style.Theme_RideBus_Light_Blue
                LightThemeVariant.pink -> R.style.Theme_RideBus_Light_Pink
                LightThemeVariant.orange -> R.style.Theme_RideBus_Light_Orange
            }
        }
        setTheme(themeId)
        super.onCreate(savedInstanceState)
    }

}