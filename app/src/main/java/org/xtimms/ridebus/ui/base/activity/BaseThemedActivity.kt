package org.xtimms.ridebus.ui.base.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.xtimms.ridebus.R
import org.xtimms.ridebus.data.preference.PreferenceValues
import org.xtimms.ridebus.data.preference.PreferencesHelper
import uy.kohesive.injekt.injectLazy

abstract class BaseThemedActivity : AppCompatActivity() {

    val preferences: PreferencesHelper by injectLazy()

    override fun onCreate(savedInstanceState: Bundle?) {
        applyAppTheme(preferences)
        super.onCreate(savedInstanceState)
    }

    companion object {
        fun AppCompatActivity.applyAppTheme(preferences: PreferencesHelper) {
            val resIds = mutableListOf<Int>()
            when (preferences.appTheme().get()) {
                PreferenceValues.AppTheme.MONET -> {
                    resIds += R.style.Theme_RideBus_Monet
                }
                PreferenceValues.AppTheme.DEFAULT -> {
                    resIds += R.style.Theme_RideBus
                }
                PreferenceValues.AppTheme.DARK_BLUE -> {
                    resIds += R.style.Theme_RideBus_DarkBlue
                    resIds += R.style.ThemeOverlay_RideBus_ColoredBars
                }
                PreferenceValues.AppTheme.GREEN_APPLE -> {
                    resIds += R.style.Theme_RideBus_GreenApple
                }
                PreferenceValues.AppTheme.ORANGE -> {
                    resIds += R.style.Theme_RideBus_Orange
                }
                PreferenceValues.AppTheme.PINK -> {
                    resIds += R.style.Theme_RideBus_Pink
                }
                PreferenceValues.AppTheme.TEAL -> {
                    resIds += R.style.Theme_RideBus_Teal
                }
            }

            if (preferences.themeDarkAmoled().get()) {
                resIds += R.style.ThemeOverlay_RideBus_Amoled
            }

            resIds.forEach {
                setTheme(it)
            }
        }
    }
}
