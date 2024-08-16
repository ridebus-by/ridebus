package org.xtimms.ridebus.data.repository

import kotlinx.coroutines.flow.Flow
import org.xtimms.ridebus.data.model.Route

typealias Routes = List<Route>

interface RouteRepository {

    fun getRoutes(): Flow<Routes>
    fun getRoute(id: Int): Route
}
