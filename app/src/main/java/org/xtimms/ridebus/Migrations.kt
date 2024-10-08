package org.xtimms.ridebus

import org.xtimms.ridebus.data.preference.PreferencesHelper
import org.xtimms.ridebus.data.updater.AppUpdateJob

object Migrations {

    var isDatabaseSchemaChanged: Boolean = false

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

            // Fresh install
            if (oldVersion == 0) {
                return false
            }

            if (oldVersion < 3) {
                // Since version 0.3 checks city from database
                when (preferences.city().get()) {
                    "POLOTSK" -> preferences.city().set("0")
                    "NOVOPOLOTSK" -> preferences.city().set("1")
                    else -> preferences.city().set("0")
                }
                preferences.databaseVersion().set("3.0")
                isDatabaseSchemaChanged = true
            }
            return true
        }
        isDatabaseSchemaChanged = false
        return false
    }
}
