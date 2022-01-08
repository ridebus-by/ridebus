package org.xtimms.ridebus.ui.setting

import androidx.preference.PreferenceScreen
import org.xtimms.ridebus.R
import org.xtimms.ridebus.data.preference.PreferenceKeys
import org.xtimms.ridebus.util.preference.*

class SettingsAccessibilityController : SettingsController() {

    override fun setupPreferenceScreen(screen: PreferenceScreen) = screen.apply {
        titleRes = R.string.pref_category_accessibility

        switchPreference {
            key = PreferenceKeys.reducedMotion
            titleRes = R.string.enable_reduced_motion
            summaryRes = R.string.enable_reduced_motion_summary
            defaultValue = false
            onChange { newValue ->
                activity?.recreate()
                true
            }
        }

        infoPreference(R.string.accessibility_info)
    }
}
