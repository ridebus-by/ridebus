package org.xtimms.ridebus

import android.app.Application
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.room.Room
import org.xtimms.ridebus.data.database.AppDatabase
import org.xtimms.ridebus.data.notification.Notifications
import org.xtimms.ridebus.util.system.LocaleHelper
import timber.log.Timber
import uy.kohesive.injekt.Injekt

open class App : Application(), LifecycleObserver {

    private var database: AppDatabase? = null

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())

        Injekt.importModule(AppModule(this))

        setupNotificationChannels()

        LocaleHelper.updateConfiguration(this, resources.configuration)

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

        AppDatabase.copyDatabase(applicationContext, AppDatabase.DATABASE_NAME);

        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, AppDatabase.DATABASE_NAME
        ).build()
    }

    protected open fun setupNotificationChannels() {
        Notifications.createChannels(this)
    }

}