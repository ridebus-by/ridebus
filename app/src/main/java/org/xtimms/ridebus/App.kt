package org.xtimms.ridebus

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.launchIn
import logcat.AndroidLogcatLogger
import logcat.LogPriority
import logcat.LogcatLogger
import org.acra.config.httpSender
import org.acra.data.StringFormat
import org.acra.ktx.initAcra
import org.xtimms.ridebus.data.notification.Notifications
import org.xtimms.ridebus.data.preference.PreferenceValues
import org.xtimms.ridebus.data.preference.PreferencesHelper
import org.xtimms.ridebus.util.preference.asImmediateFlow
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.injectLazy

open class App : Application(), DefaultLifecycleObserver {

    private val preferences: PreferencesHelper by injectLazy()

    override fun onCreate() {
        super<Application>.onCreate()

        Injekt.importModule(AppModule(this))

        setupNotificationChannels()

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

        preferences.themeMode()
            .asImmediateFlow {
                AppCompatDelegate.setDefaultNightMode(
                    when (it) {
                        PreferenceValues.ThemeMode.light -> AppCompatDelegate.MODE_NIGHT_NO
                        PreferenceValues.ThemeMode.dark -> AppCompatDelegate.MODE_NIGHT_YES
                        PreferenceValues.ThemeMode.system -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                    }
                )
            }.launchIn(ProcessLifecycleOwner.get().lifecycleScope)

        if (!LogcatLogger.isInstalled && preferences.verboseLogging()) {
            LogcatLogger.install(AndroidLogcatLogger(LogPriority.VERBOSE))
        }
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        setupAcra()
    }

    protected open fun setupAcra() {
        initAcra {
            buildConfigClass = BuildConfig::class.java
            reportFormat = StringFormat.JSON
            excludeMatchingSharedPreferencesKeys =
                listOf(".*username.*", ".*password.*", ".*token.*")
            httpSender {
                uri = BuildConfig.ACRA_URI
                basicAuthLogin = BuildConfig.ACRA_AUTH_LOGIN
                basicAuthPassword = BuildConfig.ACRA_AUTH_PASSWORD
            }
        }
    }

    protected open fun setupNotificationChannels() {
        Notifications.createChannels(this)
    }
}
