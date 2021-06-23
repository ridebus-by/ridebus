package org.xtimms.ridebus.ui.setting

import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.preference.PreferenceScreen
import kotlinx.coroutines.flow.launchIn
import org.xtimms.ridebus.R
import org.xtimms.ridebus.data.preference.asImmediateFlow
import org.xtimms.ridebus.util.preference.*
import org.xtimms.ridebus.util.system.LocaleHelper
import java.util.*
import org.xtimms.ridebus.data.preference.PreferenceKeys as Keys
import org.xtimms.ridebus.data.preference.PreferenceValues as Values

class SettingsGeneralController : SettingsController() {

    override fun setupPreferenceScreen(screen: PreferenceScreen) = screen.apply {
        titleRes = R.string.pref_category_general

        intListPreference {
            key = Keys.startScreen
            titleRes = R.string.pref_start_screen
            entriesRes = arrayOf(
                R.string.title_routes,
                R.string.title_stops,
                R.string.title_favorite
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
        switchPreference {
            key = Keys.hideBottomBar
            titleRes = R.string.pref_hide_bottom_bar_on_scroll
            defaultValue = true
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
            titleRes = R.string.pref_category_theme

            listPreference {
                key = Keys.themeMode
                titleRes = R.string.pref_theme_mode

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    entriesRes = arrayOf(
                        R.string.theme_system,
                        R.string.theme_dark_mode_off,
                        R.string.theme_dark_mode_on
                    )
                    entryValues = arrayOf(
                        Values.ThemeMode.system.name,
                        Values.ThemeMode.light.name,
                        Values.ThemeMode.dark.name
                    )
                    defaultValue = Values.ThemeMode.system.name
                } else {
                    entriesRes = arrayOf(
                        R.string.theme_light,
                        R.string.theme_dark
                    )
                    entryValues = arrayOf(
                        Values.ThemeMode.light.name,
                        Values.ThemeMode.dark.name
                    )
                    defaultValue = Values.ThemeMode.light.name
                }

                summary = "%s"

                onChange {
                    activity?.recreate()
                    true
                }
            }
            listPreference {
                key = Keys.themeLight
                titleRes = R.string.pref_theme_light
                entriesRes = arrayOf(
                    R.string.theme_light_default,
                    R.string.theme_light_blue,
                    R.string.theme_light_pink,
                    R.string.theme_light_orange
                )
                entryValues = arrayOf(
                    Values.LightThemeVariant.default.name,
                    Values.LightThemeVariant.blue.name,
                    Values.LightThemeVariant.pink.name,
                    Values.LightThemeVariant.orange.name
                )
                defaultValue = Values.LightThemeVariant.default.name
                summary = "%s"

                preferences.themeMode().asImmediateFlow { isVisible = it != Values.ThemeMode.dark }
                    .launchIn(viewScope)

                onChange {
                    if (preferences.themeMode().get() != Values.ThemeMode.dark) {
                        activity?.recreate()
                    }
                    true
                }
            }
            listPreference {
                key = Keys.themeDark
                titleRes = R.string.pref_theme_dark
                entriesRes = arrayOf(
                    R.string.theme_dark_default,
                    R.string.theme_dark_blue,
                    R.string.theme_dark_greenapple,
                    R.string.theme_dark_amoled
                )
                entryValues = arrayOf(
                    Values.DarkThemeVariant.default.name,
                    Values.DarkThemeVariant.blue.name,
                    Values.DarkThemeVariant.greenapple.name,
                    Values.DarkThemeVariant.amoled.name
                )
                defaultValue = Values.DarkThemeVariant.default.name
                summary = "%s"

                preferences.themeMode().asImmediateFlow { isVisible = it != Values.ThemeMode.light }
                    .launchIn(viewScope)

                onChange {
                    if (preferences.themeMode().get() != Values.ThemeMode.light) {
                        activity?.recreate()
                    }
                    true
                }
            }
        }

        preferenceCategory {
            titleRes = R.string.pref_category_locale

            listPreference {
                key = Keys.lang
                titleRes = R.string.pref_language

                val langs = mutableListOf<Pair<String, String>>()
                langs += Pair(
                    "",
                    "${context.getString(R.string.system_default)} (${LocaleHelper.getDisplayName("")})"
                )
                // Due to compatibility issues:
                // - Hebrew: `he` is copied into `iw` at build time
                langs += arrayOf(
                    "be",
                    "en-US",
                    "ru"
                )
                    .map {
                        Pair(it, LocaleHelper.getDisplayName(it))
                    }
                    .sortedBy { it.second }

                entryValues = langs.map { it.first }.toTypedArray()
                entries = langs.map { it.second }.toTypedArray()
                defaultValue = ""
                summary = "%s"

                onChange { newValue ->
                    val activity = activity ?: return@onChange false
                    val app = activity.application
                    LocaleHelper.changeLocale(newValue.toString())
                    LocaleHelper.updateConfiguration(app, app.resources.configuration)
                    activity.recreate()
                    true
                }
            }

            listPreference {
                key = Keys.dateFormat
                titleRes = R.string.pref_date_format
                entryValues = arrayOf("", "MM/dd/yy", "dd/MM/yy", "yyyy-MM-dd", "dd MMM yyyy", "MMM dd, yyyy")

                val now = Date().time
                entries = entryValues.map { value ->
                    val formattedDate = preferences.dateFormat(value.toString()).format(now)
                    if (value == "") {
                        "${context.getString(R.string.system_default)} ($formattedDate)"
                    } else {
                        "$value ($formattedDate)"
                    }
                }.toTypedArray()

                defaultValue = ""
                summary = "%s"
            }
        }
    }
}