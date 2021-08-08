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
import org.xtimms.ridebus.util.system.isTablet
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
        if (context.isTablet()) {
            intListPreference {
                key = Keys.sideNavIconAlignment
                titleRes = R.string.pref_side_nav_icon_alignment
                entriesRes = arrayOf(
                    R.string.alignment_top,
                    R.string.alignment_center,
                    R.string.alignment_bottom,
                )
                entryValues = arrayOf("0", "1", "2")
                defaultValue = "0"
                summary = "%s"
            }
        } else {
            switchPreference {
                key = Keys.hideBottomBarOnScroll
                titleRes = R.string.pref_hide_bottom_bar_on_scroll
                defaultValue = true
            }
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
                        R.string.theme_light,
                        R.string.theme_dark
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
            }
            listPreference {
                key = Keys.appTheme
                titleRes = R.string.pref_app_theme

                val appThemes = Values.AppTheme.values().filter {
                    val monetFilter = if (it == Values.AppTheme.MONET) {
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                    } else {
                        true
                    }
                    it.titleResId != null && monetFilter
                }
                entriesRes = appThemes.map { it.titleResId!! }.toTypedArray()
                entryValues = appThemes.map { it.name }.toTypedArray()
                defaultValue = appThemes[0].name
                summary = "%s"

                onChange {
                    activity?.recreate()
                    true
                }
            }
            switchPreference {
                key = Keys.themeDarkAmoled
                titleRes = R.string.pref_dark_theme_pure_black
                defaultValue = false

                preferences.themeMode().asImmediateFlow { isVisible = it != Values.ThemeMode.light }
                    .launchIn(viewScope)

                onChange {
                    activity?.recreate()
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