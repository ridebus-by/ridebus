package org.xtimms.ridebus.data.updater

import android.content.Context
import org.xtimms.ridebus.BuildConfig
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

class AppUpdateChecker {

    private val networkService: NetworkHelper by injectLazy()
    private val preferences: PreferencesHelper by injectLazy()

    suspend fun checkForUpdate(context: Context, isUserPrompt: Boolean = false): AppUpdateResult {
        if (isUserPrompt.not() && Date().time < preferences.lastAppCheck().get() + TimeUnit.DAYS.toMillis(1)) {
            return AppUpdateResult.NoNewUpdate
        }

        return withIOContext {
            val result = networkService.client
                .newCall(GET("https://api.github.com/repos/ridebus-by/ridebus/releases/latest"))
                .await()
                .parseAs<GithubRelease>()
                .let {
                    preferences.lastAppCheck().set(Date().time)
                    if (SemanticVersioning.isNewVersion(it.version, BuildConfig.VERSION_NAME)) {
                        AppUpdateResult.NewUpdate(it)
                    } else {
                        AppUpdateResult.NoNewUpdate
                    }
                }

            when (result) {
                is AppUpdateResult.NewUpdate -> AppUpdateNotifier(context).promptUpdate(result.release)
                else -> {}
            }

            result
        }
    }
}

val RELEASE_URL = "https://github.com/ridebus-by/ridebus/releases/tag/v${BuildConfig.VERSION_NAME}"
