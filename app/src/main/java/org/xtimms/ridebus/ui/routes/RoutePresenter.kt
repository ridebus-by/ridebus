package org.xtimms.ridebus.ui.routes

import android.os.Bundle
import kotlinx.coroutines.launch
import org.xtimms.ridebus.data.database.RideBusDatabase
import org.xtimms.ridebus.data.preference.PreferencesHelper
import org.xtimms.ridebus.ui.base.presenter.BasePresenter
import org.xtimms.ridebus.util.lang.withUIContext
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class RoutePresenter(
    private val transportType: Int,
    private val db: RideBusDatabase = Injekt.get(),
    private val preferences: PreferencesHelper = Injekt.get()
) : BasePresenter<RouteController>() {

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        getRoutes(transportType)
    }

    private fun getRoutes(transportType: Int) {
        presenterScope.launch {
            val routes = db.routeDao().getRoutes(transportType, preferences.city().get().toInt())
            withUIContext {
                view?.setRoutes(routes.map(::RouteItem))
            }
        }
    }
}
