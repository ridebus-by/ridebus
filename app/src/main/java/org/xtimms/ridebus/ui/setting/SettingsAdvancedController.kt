package org.xtimms.ridebus.ui.setting

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.core.net.toUri
import androidx.preference.PreferenceScreen
import org.xtimms.ridebus.R
import org.xtimms.ridebus.data.preference.PreferenceValues
import org.xtimms.ridebus.ui.base.controller.openInBrowser
import org.xtimms.ridebus.util.CrashLogUtil
import org.xtimms.ridebus.util.preference.*
import org.xtimms.ridebus.util.system.powerManager
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            preferenceCategory {
                titleRes = R.string.label_background_activity

                preference {
                    key = "pref_disable_battery_optimization"
                    titleRes = R.string.pref_disable_battery_optimization
                    summaryRes = R.string.pref_disable_battery_optimization_summary

                    onClick {
                        val packageName: String = context.packageName
                        if (!context.powerManager.isIgnoringBatteryOptimizations(packageName)) {
                            try {
                                val intent = Intent().apply {
                                    action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                                    data = "package:$packageName".toUri()
                                }
                                startActivity(intent)
                            } catch (e: ActivityNotFoundException) {
                                context.toast(R.string.battery_optimization_setting_activity_not_found)
                            }
                        } else {
                            context.toast(R.string.battery_optimization_disabled)
                        }
                    }
                }

                preference {
                    key = "pref_dont_kill_my_app"
                    title = "Don't kill my app!"
                    summaryRes = R.string.about_dont_kill_my_app

                    onClick {
                        openInBrowser("https://dontkillmyapp.com/")
                    }
                }
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
