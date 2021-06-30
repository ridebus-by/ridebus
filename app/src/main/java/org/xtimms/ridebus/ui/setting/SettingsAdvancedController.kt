package org.xtimms.ridebus.ui.setting

import android.annotation.SuppressLint
import androidx.preference.PreferenceScreen
import org.xtimms.ridebus.R
import org.xtimms.ridebus.util.CrashLogUtil
import org.xtimms.ridebus.util.preference.onClick
import org.xtimms.ridebus.util.preference.preference
import org.xtimms.ridebus.util.preference.summaryRes
import org.xtimms.ridebus.util.preference.titleRes

class SettingsAdvancedController : SettingsController() {

    override fun setupPreferenceScreen(screen: PreferenceScreen) = screen.apply {
        titleRes = R.string.pref_category_advanced

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