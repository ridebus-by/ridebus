package org.xtimms.ridebus.ui.base.activity

import android.app.Activity
import org.xtimms.ridebus.R
import org.xtimms.ridebus.data.preference.PreferenceValues
import org.xtimms.ridebus.data.preference.PreferencesHelper
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

interface ThemingDelegate {

    fun applyAppTheme(activity: Activity)

    companion object {
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
                PreferenceValues.AppTheme.PINK -> {
                    resIds += R.style.Theme_RideBus_Balloon
                }
                PreferenceValues.AppTheme.POMEGRANATE -> {
                    resIds += R.style.Theme_RideBus_Pomegranate
                }
                PreferenceValues.AppTheme.HONEY -> {
                    resIds += R.style.Theme_RideBus_Honey
                }
                PreferenceValues.AppTheme.GALAXY -> {
                    resIds += R.style.Theme_RideBus_Galaxy
                }
            }

            if (isAmoled) {
                resIds += R.style.ThemeOverlay_RideBus_Amoled
            }

            return resIds
        }
    }
}

class ThemingDelegateImpl : ThemingDelegate {
    override fun applyAppTheme(activity: Activity) {
        val preferences = Injekt.get<PreferencesHelper>()
        ThemingDelegate.getThemeResIds(preferences.appTheme().get(), preferences.themeDarkAmoled().get())
            .forEach { activity.setTheme(it) }
    }
}
