package org.xtimms.ridebus

import android.app.Application
import androidx.core.content.ContextCompat
import org.xtimms.ridebus.data.database.RideBusDatabase
import org.xtimms.ridebus.data.preference.PreferencesHelper
import uy.kohesive.injekt.api.*

class AppModule(val app: Application) : InjektModule {

    override fun InjektRegistrar.registerInjectables() {
        addSingleton(app)

        addSingletonFactory { PreferencesHelper(app) }

        addSingletonFactory { RideBusDatabase.getDatabase(app) }

        ContextCompat.getMainExecutor(app).execute {
            get<PreferencesHelper>()

            get<RideBusDatabase>()
        }
    }
}
