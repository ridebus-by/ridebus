package org.xtimms.ridebus

import org.xtimms.ridebus.data.preference.PreferencesHelper
import org.xtimms.ridebus.data.updater.app.AppUpdateJob
import org.xtimms.ridebus.data.updater.database.DatabaseUpdateJob

object Migrations {

    /**
     * Performs a migration when the application is updated.
     *
     * @param preferences Preferences of the application.
     * @return true if a migration is performed, false otherwise.
     */
    fun upgrade(preferences: PreferencesHelper): Boolean {
        val context = preferences.context

        val oldVersion = preferences.lastVersionCode().get()
        if (oldVersion < BuildConfig.VERSION_CODE) {
            preferences.lastVersionCode().set(BuildConfig.VERSION_CODE)

            // Always set up background tasks to ensure they're running
            if (BuildConfig.INCLUDE_UPDATER) {
                AppUpdateJob.setupTask(context)
            }
            DatabaseUpdateJob.setupTask(context)

            // Fresh install
            if (oldVersion == 0) {
                return false
            }
            return true
        }
        return false
    }
}
