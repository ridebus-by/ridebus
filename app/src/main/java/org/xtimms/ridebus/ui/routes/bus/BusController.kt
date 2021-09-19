package org.xtimms.ridebus.ui.routes.bus

import org.xtimms.ridebus.data.database.RideBusDatabase
import org.xtimms.ridebus.data.preference.PreferencesHelper
import org.xtimms.ridebus.ui.routes.RouteController
import org.xtimms.ridebus.ui.routes.RouteItem
import uy.kohesive.injekt.injectLazy

class BusController : RouteController() {

    private val db: RideBusDatabase by injectLazy()

    private val preferences: PreferencesHelper by injectLazy()

    override fun getRoutes(): List<RouteItem> {
        return db.routeDao().getBuses(preferences.city().defaultValue.ordinal).map { RouteItem(it) }
    }
}
