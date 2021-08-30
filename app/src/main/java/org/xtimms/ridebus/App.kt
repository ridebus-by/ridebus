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
import org.xtimms.ridebus.data.database.RideBusDatabase
import org.xtimms.ridebus.data.notification.Notifications
import org.xtimms.ridebus.data.preference.PreferenceValues
import org.xtimms.ridebus.data.preference.PreferencesHelper
import org.xtimms.ridebus.data.preference.asImmediateFlow
import timber.log.Timber
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.injectLazy

open class App : Application(), LifecycleObserver {

    private var instance: App? = null
    private val db: RideBusDatabase? = null
    private val preferences: PreferencesHelper by injectLazy()

    open fun getInstance(): App? {
        return instance
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())

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
    }

    open fun getDatabase(): RideBusDatabase? {
        return db
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
                arrayOf(".*username.*", ".*password.*", ".*token.*")
            httpSender {
                uri = BuildConfig.ACRA_URI
            }
        }
    }

    protected open fun setupNotificationChannels() {
        Notifications.createChannels(this)
    }
}
