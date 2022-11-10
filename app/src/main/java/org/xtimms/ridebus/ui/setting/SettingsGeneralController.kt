package org.xtimms.ridebus.ui.setting

import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.preference.PreferenceScreen
import org.xtimms.ridebus.R
import org.xtimms.ridebus.util.preference.*
import org.xtimms.ridebus.data.preference.PreferenceKeys as Keys

class SettingsGeneralController : SettingsController() {

    override fun setupPreferenceScreen(screen: PreferenceScreen) = screen.apply {
        titleRes = R.string.pref_category_general

        intListPreference {
            key = Keys.startScreen
            titleRes = R.string.pref_start_screen
            entriesRes = arrayOf(
                R.string.title_routes,
                R.string.title_stops,
                R.string.title_favourite
            )
            entryValues = arrayOf("1", "2", "3")
            defaultValue = "1"
            summary = "%s"
        }

        switchPreference {
            key = Keys.confirmExit
            titleRes = R.string.pref_confirm_exit
            defaultValue = false
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            preference {
                key = "pref_manage_notifications"
                titleRes = R.string.pref_manage_notifications
                onClick {
                    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                    }
                    startActivity(intent)
                }
            }
        }

        preferenceCategory {
            titleRes = R.string.pref_category_locale

            switchPreference {
                bindTo(preferences.transliterate())
                titleRes = R.string.transliterate
                summaryRes = R.string.transliterate_summary
            }
        }
    }
}
