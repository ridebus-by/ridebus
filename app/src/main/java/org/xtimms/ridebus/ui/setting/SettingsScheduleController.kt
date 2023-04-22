package org.xtimms.ridebus.ui.setting

import androidx.preference.PreferenceScreen
import logcat.LogPriority
import org.xtimms.ridebus.R
import org.xtimms.ridebus.data.database.RideBusDatabase
import org.xtimms.ridebus.data.updater.database.DatabaseUpdateChecker
import org.xtimms.ridebus.data.updater.database.DatabaseUpdateJob
import org.xtimms.ridebus.data.updater.database.DatabaseUpdateResult
import org.xtimms.ridebus.ui.more.CriticalDatabaseUpdateDialogController
import org.xtimms.ridebus.ui.more.NewScheduleDialogController
import org.xtimms.ridebus.util.lang.launchNow
import org.xtimms.ridebus.util.preference.*
import org.xtimms.ridebus.util.system.logcat
import org.xtimms.ridebus.util.system.toast
import uy.kohesive.injekt.injectLazy

//
// Created by Xtimms on 28.08.2021.
//
class SettingsScheduleController : SettingsController() {

    private val database: RideBusDatabase by injectLazy()
    private val updateChecker by lazy { DatabaseUpdateChecker() }

    override fun setupPreferenceScreen(screen: PreferenceScreen) = screen.apply {
        titleRes = R.string.pref_category_schedule

        switchPreference {
            bindTo(preferences.autoUpdateSchedule())
            titleRes = R.string.automatic_schedule_updates
            summaryRes = R.string.automatic_schedule_updates_summary

            onChange { newValue ->
                val checked = newValue as Boolean
                DatabaseUpdateJob.setupTask(activity!!, checked)
                true
            }
        }

        preference {
            key = "pref_about_check_database_for_updates"
            titleRes = R.string.check_for_new_database

            onClick { checkVersion() }
        }

        listPreference {
            bindTo(preferences.city())
            titleRes = R.string.city
            entriesName = database.cityDao().getCitiesNames().map { it }.toTypedArray()
            entryValues = database.cityDao().getCitiesIds().map { it.toString() }.toTypedArray()
            summary = "%s"
        }

        infoPreference(R.string.schedule_info)
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
                    is DatabaseUpdateResult.NewUpdate -> {
                        NewScheduleDialogController(result).showDialog(router)
                    }
                    is DatabaseUpdateResult.CriticalUpdate -> {
                        CriticalDatabaseUpdateDialogController(result).showDialog(router)
                    }
                    is DatabaseUpdateResult.NoNewUpdate -> {
                        activity?.toast(R.string.update_check_no_new_updates)
                    }
                }
            } catch (error: Exception) {
                activity?.toast(error.message)
                logcat(LogPriority.ERROR, error)
            }
        }
    }
}
