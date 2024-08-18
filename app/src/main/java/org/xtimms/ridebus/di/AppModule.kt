package org.xtimms.ridebus.di

import android.app.Application
import androidx.core.content.ContextCompat
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.serialization.json.Json
import org.xtimms.ridebus.data.database.RideBusDatabase
import org.xtimms.ridebus.data.preference.PreferencesHelper
import org.xtimms.ridebus.data.repository.RouteRepository
import org.xtimms.ridebus.data.repository.StopRepository
import org.xtimms.ridebus.data.repository.impl.RouteRepositoryImpl
import org.xtimms.ridebus.data.repository.impl.StopRepositoryImpl
import org.xtimms.ridebus.data.usecases.GetRoute
import org.xtimms.ridebus.data.usecases.GetRoutes
import org.xtimms.ridebus.data.usecases.GetStops
import org.xtimms.ridebus.data.usecases.UseCases
import org.xtimms.ridebus.network.NetworkHelper
import uy.kohesive.injekt.api.InjektModule
import uy.kohesive.injekt.api.InjektRegistrar
import uy.kohesive.injekt.api.addSingleton
import uy.kohesive.injekt.api.addSingletonFactory
import uy.kohesive.injekt.api.get

class AppModule(val app: Application) : InjektModule {

    override fun InjektRegistrar.registerInjectables() {
        addSingleton(app)

        addSingletonFactory { Json { ignoreUnknownKeys = true } }

        addSingletonFactory { PreferencesHelper(app) }

        addSingletonFactory { RideBusDatabase.getDatabase(app) }

        addSingletonFactory { NetworkHelper(app) }

        addSingletonFactory { Firebase.firestore.collection("schedule") }

        addSingletonFactory<RouteRepository> { RouteRepositoryImpl(get()) }
        addSingletonFactory<StopRepository> { StopRepositoryImpl(get()) }

        addSingletonFactory { GetRoutes(get()) }
        addSingletonFactory { GetRoute(get()) }
        addSingletonFactory { GetStops(get()) }

        addSingletonFactory { UseCases(get(), get(), get()) }

        ContextCompat.getMainExecutor(app).execute {
            get<PreferencesHelper>()

            get<NetworkHelper>()

            get<RideBusDatabase>()
        }
    }
}
