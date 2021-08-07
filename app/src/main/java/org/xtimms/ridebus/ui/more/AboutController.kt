package org.xtimms.ridebus.ui.more

import androidx.preference.PreferenceScreen
import org.xtimms.ridebus.BuildConfig
import org.xtimms.ridebus.R
import org.xtimms.ridebus.ui.base.controller.NoToolbarElevationController
import org.xtimms.ridebus.ui.base.controller.withFadeTransaction
import org.xtimms.ridebus.ui.more.licenses.LicensesController
import org.xtimms.ridebus.ui.setting.SettingsController
import org.xtimms.ridebus.util.CrashLogUtil
import org.xtimms.ridebus.util.lang.toDateTimestampString
import org.xtimms.ridebus.util.preference.add
import org.xtimms.ridebus.util.preference.onClick
import org.xtimms.ridebus.util.preference.preference
import org.xtimms.ridebus.util.preference.titleRes
import org.xtimms.ridebus.util.system.copyToClipboard
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class AboutController : SettingsController(), NoToolbarElevationController {

    private val dateFormat: DateFormat = preferences.dateFormat()

    override fun setupPreferenceScreen(screen: PreferenceScreen) = screen.apply {
        titleRes = R.string.pref_category_about

        add(MoreHeaderPreference(context))

        preference {
            key = "pref_about_version"
            titleRes = R.string.version
            summary = if (BuildConfig.DEBUG) {
                "Preview r${BuildConfig.COMMIT_COUNT} (${BuildConfig.COMMIT_SHA}, ${getFormattedBuildTime()})"
            } else {
                "Stable ${BuildConfig.VERSION_NAME} (${getFormattedBuildTime()})"
            }

            onClick {
                activity?.let {
                    val deviceInfo = CrashLogUtil(it).getDebugInfo()
                    it.copyToClipboard("Debug information", deviceInfo)
                }
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