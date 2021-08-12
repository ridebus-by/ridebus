package org.xtimms.ridebus

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.launchIn
import org.acra.config.httpSender
import org.acra.data.StringFormat
import org.acra.ktx.initAcra
import org.xtimms.ridebus.data.notification.Notifications
import org.xtimms.ridebus.data.preference.PreferenceValues
import org.xtimms.ridebus.data.preference.PreferencesHelper
import org.xtimms.ridebus.data.preference.asImmediateFlow
import org.xtimms.ridebus.util.system.LocaleHelper
import timber.log.Timber
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.injectLazy

open class App : Application(), LifecycleObserver {

    private val preferences: PreferencesHelper by injectLazy()

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())

        Injekt.importModule(AppModule(this))

        setupNotificationChannels()

        LocaleHelper.updateConfiguration(this, resources.configuration)

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
    }

    protected open fun setupNotificationChannels() {
        Notifications.createChannels(this)
    }
}
