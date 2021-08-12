package org.xtimms.ridebus.ui.setting

import androidx.preference.PreferenceScreen
import org.xtimms.ridebus.R
import org.xtimms.ridebus.util.CrashLogUtil
import org.xtimms.ridebus.util.preference.*

class SettingsAdvancedController : SettingsController() {

    override fun setupPreferenceScreen(screen: PreferenceScreen) = screen.apply {
        titleRes = R.string.pref_category_advanced

        switchPreference {
            key = "acra.enable"
            titleRes = R.string.pref_enable_acra
            summaryRes = R.string.pref_acra_summary
            defaultValue = true
        }

        preference {
            key = "dump_crash_logs"
            titleRes = R.string.pref_dump_crash_logs
            summaryRes = R.string.pref_dump_crash_logs_summary

            onClick {
                CrashLogUtil(context).dumpLogs()
            }
        }
    }
}
