package org.xtimms.ridebus.ui.routes

import org.xtimms.ridebus.data.database.RideBusDatabase
import org.xtimms.ridebus.data.preference.PreferencesHelper
import uy.kohesive.injekt.injectLazy

class TramController : RouteController() {

    private val db: RideBusDatabase by injectLazy()

    private val preferences: PreferencesHelper by injectLazy()

    override fun getRoutes(): List<RouteItem> {
        return db.routeDao().getTrams(preferences.city().get().ordinal).map { RouteItem(it) }
    }
}
