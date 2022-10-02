package org.xtimms.ridebus

import android.app.Application
import androidx.core.content.ContextCompat
import kotlinx.serialization.json.Json
import org.xtimms.ridebus.data.database.RideBusDatabase
import org.xtimms.ridebus.data.preference.PreferencesHelper
import org.xtimms.ridebus.network.NetworkHelper
import uy.kohesive.injekt.api.*

class AppModule(val app: Application) : InjektModule {

    override fun InjektRegistrar.registerInjectables() {
        addSingleton(app)

        addSingletonFactory { Json { ignoreUnknownKeys = true } }

        addSingletonFactory { PreferencesHelper(app) }

        addSingletonFactory { RideBusDatabase.getDatabase(app) }

        addSingletonFactory { NetworkHelper(app) }

        ContextCompat.getMainExecutor(app).execute {
            get<PreferencesHelper>()

            get<NetworkHelper>()

            get<RideBusDatabase>()
        }
    }
}
