package org.xtimms.ridebus.data.updater.database

import android.content.Context
import org.xtimms.ridebus.data.preference.PreferencesHelper
import org.xtimms.ridebus.network.GET
import org.xtimms.ridebus.network.NetworkHelper
import org.xtimms.ridebus.network.await
import org.xtimms.ridebus.network.parseAs
import org.xtimms.ridebus.util.SemanticVersioning
import org.xtimms.ridebus.util.lang.withIOContext
import uy.kohesive.injekt.injectLazy
import java.util.Date
import java.util.concurrent.TimeUnit

class DatabaseUpdateChecker {

    private val networkService: NetworkHelper by injectLazy()
    private val preferences: PreferencesHelper by injectLazy()

    suspend fun checkForUpdate(
        context: Context,
        isUserPrompt: Boolean = false
    ): DatabaseUpdateResult {
        if (isUserPrompt.not() && Date().time < preferences.lastDatabaseCheck()
            .get() + TimeUnit.DAYS.toMillis(1)
        ) {
            return DatabaseUpdateResult.NoNewUpdate
        }

        return withIOContext {
            val result = networkService.client
                .newCall(GET("https://raw.githubusercontent.com/ridebus-by/database/v3/index.json"))
                .await()
                .parseAs<GithubDatabase>()
                .let {
                    preferences.lastDatabaseCheck().set(Date().time)
                    if (SemanticVersioning.isNewVersion(
                            it.version,
                            preferences.databaseVersion().get()
                        )
                    ) {
                        DatabaseUpdateResult.NewUpdate(it)
                    } else {
                        DatabaseUpdateResult.NoNewUpdate
                    }
                }

            when (result) {
                is DatabaseUpdateResult.NewUpdate -> DatabaseUpdateNotifier(context).promptUpdate(
                    result.update
                )
                else -> {}
            }
            result
        }
    }
}
