package org.xtimms.ridebus.ui.more

import androidx.preference.PreferenceScreen
import logcat.LogPriority
import org.xtimms.ridebus.BuildConfig
import org.xtimms.ridebus.R
import org.xtimms.ridebus.data.updater.app.AppUpdateChecker
import org.xtimms.ridebus.data.updater.app.AppUpdateResult
import org.xtimms.ridebus.data.updater.app.RELEASE_URL
import org.xtimms.ridebus.ui.base.controller.NoAppBarElevationController
import org.xtimms.ridebus.ui.base.controller.openInBrowser
import org.xtimms.ridebus.ui.base.controller.withFadeTransaction
import org.xtimms.ridebus.ui.more.licenses.LicensesController
import org.xtimms.ridebus.ui.more.sources.UsedSourcesController
import org.xtimms.ridebus.ui.setting.SettingsController
import org.xtimms.ridebus.util.CrashLogUtil
import org.xtimms.ridebus.util.lang.launchNow
import org.xtimms.ridebus.util.lang.toDateTimestampString
import org.xtimms.ridebus.util.preference.add
import org.xtimms.ridebus.util.preference.onClick
import org.xtimms.ridebus.util.preference.preference
import org.xtimms.ridebus.util.preference.titleRes
import org.xtimms.ridebus.util.system.copyToClipboard
import org.xtimms.ridebus.util.system.logcat
import org.xtimms.ridebus.util.system.toast
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

const val YANDEX_MAPS_TERMS_OF_USE = "https://yandex.ru/legal/maps_termsofuse"

class AboutController : SettingsController(), NoAppBarElevationController {

    private val updateChecker by lazy { AppUpdateChecker() }

    private val dateFormat: DateFormat = preferences.dateFormat()

    override fun setupPreferenceScreen(screen: PreferenceScreen) = screen.apply {
        titleRes = R.string.pref_category_about

        add(AboutHeaderPreference(context))

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

    /**
     * Checks version and shows a user prompt if an update is available.
     */
    private fun checkVersion() {
        if (activity == null) return

        activity!!.toast(R.string.update_check_look_for_updates)

        launchNow {
            try {
                when (val result = updateChecker.checkForUpdate(activity!!, isUserPrompt = true)) {
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

            buildTime!!.toDateTimestampString(dateFormat)
        } catch (e: Exception) {
            BuildConfig.BUILD_TIME
        }
    }
}
