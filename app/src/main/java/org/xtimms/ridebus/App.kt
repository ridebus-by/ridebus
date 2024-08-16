package org.xtimms.ridebus

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.yandex.mapkit.MapKitFactory
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
import org.xtimms.ridebus.di.AppModule
import org.xtimms.ridebus.util.preference.asImmediateFlow
import org.xtimms.ridebus.util.system.logcat
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.injectLazy

open class App : Application(), DefaultLifecycleObserver {

    private val preferences: PreferencesHelper by injectLazy()

    override fun onCreate() {
        super<Application>.onCreate()

        Injekt.importModule(AppModule(this))

        // setupAcra()
        setupNotificationChannels()

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

        MapKitFactory.setApiKey("397822a9-4d94-49cb-b402-d419ed3d5355")

        preferences.themeMode().asImmediateFlow {
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
        try {
            Notifications.createChannels(this)
        } catch (e: Exception) {
            logcat(LogPriority.ERROR, e) { "Failed to modify notification channels" }
        }
    }
}
