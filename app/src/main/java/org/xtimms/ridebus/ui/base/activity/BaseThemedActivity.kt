package org.xtimms.ridebus.ui.base.activity

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
        setTheme(getThemeResourceId(preferences))
        super.onCreate(savedInstanceState)
    }

    companion object {
        fun getThemeResourceId(preferences: PreferencesHelper): Int {
            return if (preferences.isDarkMode()) {
                when (preferences.themeDark().get()) {
                    DarkThemeVariant.default -> R.style.Theme_RideBus_Dark
                    DarkThemeVariant.blue -> R.style.Theme_RideBus_Dark_Blue
                    DarkThemeVariant.greenapple -> R.style.Theme_RideBus_Dark_GreenApple
                    DarkThemeVariant.yellow -> R.style.Theme_RideBus_Dark_Yellow
                    DarkThemeVariant.mono -> R.style.Theme_RideBus_Dark_Mono
                    DarkThemeVariant.amoled -> R.style.Theme_RideBus_Amoled
                }
            } else {
                when (preferences.themeLight().get()) {
                    LightThemeVariant.default -> R.style.Theme_RideBus_Light
                    LightThemeVariant.blue -> R.style.Theme_RideBus_Light_Blue
                    LightThemeVariant.pink -> R.style.Theme_RideBus_Light_Pink
                    LightThemeVariant.orange -> R.style.Theme_RideBus_Light_Orange
                    LightThemeVariant.mono -> R.style.Theme_RideBus_Light_Mono
                }
            }
        }
    }

}