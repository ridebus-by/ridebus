package org.xtimms.ridebus.ui.setting

import androidx.preference.PreferenceScreen
import org.xtimms.ridebus.R
import org.xtimms.ridebus.data.preference.PreferenceKeys
import org.xtimms.ridebus.data.preference.PreferenceValues
import org.xtimms.ridebus.util.preference.*

//
// Created by Xtimms on 28.08.2021.
//
class SettingsScheduleController : SettingsController() {

    override fun setupPreferenceScreen(screen: PreferenceScreen) = screen.apply {
        titleRes = R.string.pref_category_schedule

        listPreference {
            key = PreferenceKeys.city
            titleRes = R.string.city
            entriesRes = arrayOf(
                R.string.city_polotsk,
                R.string.city_novopolotsk
            )
            entryValues = arrayOf(
                PreferenceValues.City.POLOTSK.name,
                PreferenceValues.City.NOVOPOLOTSK.name
            )
            defaultValue = PreferenceValues.City.POLOTSK.name
            summary = "%s"
        }
    }
}
