package org.xtimms.ridebus.ui.base.activity

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.xtimms.ridebus.R
import org.xtimms.ridebus.data.preference.PreferenceValues
import org.xtimms.ridebus.data.preference.PreferencesHelper
import org.xtimms.ridebus.util.system.prepareTabletUiContext
import uy.kohesive.injekt.injectLazy

abstract class BaseThemedActivity : AppCompatActivity() {

    val preferences: PreferencesHelper by injectLazy()

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(newBase.prepareTabletUiContext())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        applyAppTheme(preferences)
        super.onCreate(savedInstanceState)
    }

    companion object {
        fun AppCompatActivity.applyAppTheme(preferences: PreferencesHelper) {
            getThemeResIds(preferences.appTheme().get(), preferences.themeDarkAmoled().get())
                .forEach { setTheme(it) }
        }

        fun getThemeResIds(appTheme: PreferenceValues.AppTheme, isAmoled: Boolean): List<Int> {
            val resIds = mutableListOf<Int>()
            when (appTheme) {
                PreferenceValues.AppTheme.MONET -> {
                    resIds += R.style.Theme_RideBus_Monet
                }
                PreferenceValues.AppTheme.DEFAULT -> {
                    resIds += R.style.Theme_RideBus
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
                PreferenceValues.AppTheme.POMEGRANATE -> {
                    resIds += R.style.Theme_RideBus_Pomegranate
                }
                PreferenceValues.AppTheme.SUNFLOWER -> {
                    resIds += R.style.Theme_RideBus_Sunflower
                }
                PreferenceValues.AppTheme.PROTOSS_PYLON -> {
                    resIds += R.style.Theme_RideBus_ProtossPylon
                }
                PreferenceValues.AppTheme.BLUEBELL -> {
                    resIds += R.style.Theme_RideBus_Bluebell
                }
            }

            if (isAmoled) {
                resIds += R.style.ThemeOverlay_RideBus_Amoled
            }

            return resIds
        }
    }
}
