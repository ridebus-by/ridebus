package org.xtimms.ridebus.ui.routes.taxi

import org.xtimms.ridebus.data.database.RideBusDatabase
import org.xtimms.ridebus.data.preference.PreferencesHelper
import org.xtimms.ridebus.ui.routes.RouteController
import org.xtimms.ridebus.ui.routes.RouteItem
import uy.kohesive.injekt.injectLazy

class TaxiController : RouteController() {

    private val db: RideBusDatabase by injectLazy()

    private val preferences: PreferencesHelper by injectLazy()

    override fun getRoutes(): List<RouteItem> {
        return db.routeDao().getTaxis(preferences.city().get().ordinal).map { RouteItem(it) }
    }
}
