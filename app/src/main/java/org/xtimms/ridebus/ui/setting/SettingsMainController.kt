package org.xtimms.ridebus.ui.setting

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.preference.PreferenceScreen
import kotlinx.coroutines.runBlocking
import logcat.LogPriority
import org.xtimms.ridebus.BuildConfig
import org.xtimms.ridebus.R
import org.xtimms.ridebus.data.updater.AppUpdateChecker
import org.xtimms.ridebus.data.updater.AppUpdateResult
import org.xtimms.ridebus.data.updater.RELEASE_URL
import org.xtimms.ridebus.data.usecases.UseCases
import org.xtimms.ridebus.ui.base.controller.openInBrowser
import org.xtimms.ridebus.ui.base.controller.withFadeTransaction
import org.xtimms.ridebus.ui.more.AboutLinksPreference
import org.xtimms.ridebus.ui.more.NewUpdateDialogController
import org.xtimms.ridebus.ui.more.licenses.LicensesController
import org.xtimms.ridebus.ui.more.sources.UsedSourcesController
import org.xtimms.ridebus.ui.setting.search.SettingsSearchController
import org.xtimms.ridebus.util.CrashLogUtil
import org.xtimms.ridebus.util.lang.launchNow
import org.xtimms.ridebus.util.lang.toDateTimestampString
import org.xtimms.ridebus.util.preference.*
import org.xtimms.ridebus.util.system.copyToClipboard
import org.xtimms.ridebus.util.system.getResourceColor
import org.xtimms.ridebus.util.system.logcat
import org.xtimms.ridebus.util.system.toast
import uy.kohesive.injekt.injectLazy
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

const val YANDEX_MAPS_TERMS_OF_USE = "https://yandex.ru/legal/maps_termsofuse"

class SettingsMainController : SettingsController() {

    private val useCases: UseCases by injectLazy()
    private val updateChecker by lazy { AppUpdateChecker() }
    private val dateFormat: DateFormat = preferences.dateFormat()

    override fun setupPreferenceScreen(screen: PreferenceScreen) = screen.apply {
        titleRes = R.string.label_settings

        val tintColor = context.getResourceColor(androidx.appcompat.R.attr.colorAccent)

        preferenceCategory {
            titleRes = R.string.important_settings

            listPreference {
                bindTo(preferences.city())
                iconRes = R.drawable.ic_city
                iconTint = tintColor
                titleRes = R.string.city
                entriesName = runBlocking { useCases.getCitiesNames().map { it }.toTypedArray() }
                entryValues = runBlocking { useCases.getCitiesIds().map { it.toString() }.toTypedArray() }
                summary = "%s"
            }
        }

        preferenceCategory {
            titleRes = R.string.settings_categories

            preference {
                iconRes = R.drawable.ic_tune
                iconTint = tintColor
                titleRes = R.string.pref_category_general
                onClick { navigateTo(SettingsGeneralController()) }
            }
            preference {
                iconRes = R.drawable.ic_brush
                iconTint = tintColor
                titleRes = R.string.pref_category_appearance
                onClick { navigateTo(SettingsAppearanceController()) }
            }
            preference {
                iconRes = R.drawable.ic_accessibility
                iconTint = tintColor
                titleRes = R.string.pref_category_accessibility
                onClick { navigateTo(SettingsAccessibilityController()) }
            }
            preference {
                iconRes = R.drawable.ic_code
                iconTint = tintColor
                titleRes = R.string.pref_category_advanced
                onClick { navigateTo(SettingsAdvancedController()) }
            }
        }

        preferenceCategory {
            titleRes = R.string.pref_category_about

            preference {
                key = "pref_about_build_info"
                titleRes = R.string.build_info
                summary = "${getFormattedBuildTime()} (${BuildConfig.COMMIT_SHA})"
            }
            preference {
                key = "pref_about_database_version"
                titleRes = R.string.database_version
                summary = preferences.databaseVersion().get()
            }
            preference {
                key = "pref_about_copy_debug_info"
                titleRes = R.string.copy_debug_info
                onClick {
                    activity?.let {
                        val deviceInfo = CrashLogUtil(it).getDebugInfo()
                        it.copyToClipboard("Debug information", deviceInfo)
                    }
                }
            }
            if (BuildConfig.INCLUDE_UPDATER) {
                preference {
                    key = "pref_about_check_for_updates"
                    titleRes = R.string.check_for_updates

                    onClick { checkVersion() }
                }
            }
            if (!BuildConfig.DEBUG) {
                preference {
                    key = "pref_about_whats_new"
                    titleRes = R.string.whats_new

                    onClick {
                        openInBrowser(RELEASE_URL)
                    }
                }
            }
            preference {
                key = "pref_sources_used"
                titleRes = R.string.pref_sources_used
                onClick {
                    router.pushController(UsedSourcesController().withFadeTransaction())
                }
            }
            preference {
                key = "pref_yandex_maps_terms_of_use"
                titleRes = R.string.yandex_maps_terms_of_use

                onClick {
                    openInBrowser(YANDEX_MAPS_TERMS_OF_USE)
                }
            }
            preference {
                key = "pref_about_licenses"
                titleRes = R.string.licenses
                onClick {
                    router.pushController(LicensesController().withFadeTransaction())
                }
            }

            add(AboutLinksPreference(context))
        }
    }

    private fun navigateTo(controller: SettingsController) {
        router.pushController(controller.withFadeTransaction())
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // Inflate menu
        inflater.inflate(R.menu.settings_main, menu)

        // Initialize search option.
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        searchView.maxWidth = Int.MAX_VALUE

        // Change hint to show global search.
        searchView.queryHint = applicationContext?.getString(R.string.action_search_settings)

        searchItem.setOnActionExpandListener(
            object : MenuItem.OnActionExpandListener {
                override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                    preferences.lastSearchQuerySearchSettings().set("") // reset saved search query
                    router.pushController(SettingsSearchController().withFadeTransaction())
                    return true
                }

                override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                    return true
                }
            }
        )
    }

    /**
     * Checks version and shows a user prompt if an update is available.
     */
    private fun checkVersion() {
        if (activity == null) return

        checkNotNull(activity).toast(R.string.update_check_look_for_updates)

        launchNow {
            try {
                when (val result = updateChecker.checkForUpdate(checkNotNull(activity), isUserPrompt = true)) {
                    is AppUpdateResult.NewUpdate -> {
                        NewUpdateDialogController(result).showDialog(router)
                    }
                    is AppUpdateResult.NoNewUpdate -> {
                        activity?.toast(R.string.update_check_no_new_updates)
                    }
                }
            } catch (error: Exception) {
                activity?.toast(error.message)
                logcat(LogPriority.ERROR, error)
            }
        }
    }

    private fun getFormattedBuildTime(): String {
        return try {
            val inputDf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'", Locale.US)
            inputDf.timeZone = TimeZone.getTimeZone("UTC")
            val buildTime = inputDf.parse(BuildConfig.BUILD_TIME)

            val outputDf = DateFormat.getDateTimeInstance(
                DateFormat.MEDIUM,
                DateFormat.SHORT,
                Locale.getDefault()
            )
            outputDf.timeZone = TimeZone.getDefault()

            checkNotNull(buildTime).toDateTimestampString(dateFormat)
        } catch (e: Exception) {
            BuildConfig.BUILD_TIME
        }
    }
}
