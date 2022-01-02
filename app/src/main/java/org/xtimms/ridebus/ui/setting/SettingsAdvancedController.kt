package org.xtimms.ridebus.ui.setting

import androidx.preference.PreferenceScreen
import org.xtimms.ridebus.R
import org.xtimms.ridebus.data.preference.PreferenceValues
import org.xtimms.ridebus.util.CrashLogUtil
import org.xtimms.ridebus.util.preference.*
import org.xtimms.ridebus.util.system.toast
import org.xtimms.ridebus.data.preference.PreferenceKeys as Keys

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

        switchPreference {
            key = Keys.verboseLogging
            titleRes = R.string.pref_verbose_logging
            summaryRes = R.string.pref_verbose_logging_summary
            defaultValue = false

            onChange {
                activity?.toast(R.string.requires_app_restart)
                true
            }
        }

        preferenceCategory {
            titleRes = R.string.pref_category_display

            listPreference {
                bindTo(preferences.tabletUiMode())
                titleRes = R.string.pref_tablet_ui_mode
                summary = "%s"
                entriesRes = arrayOf(R.string.lock_always, R.string.landscape, R.string.lock_never)
                entryValues = PreferenceValues.TabletUiMode.values().map { it.name }.toTypedArray()

                onChange {
                    activity?.toast(R.string.requires_app_restart)
                    true
                }
            }
        }
    }
}
