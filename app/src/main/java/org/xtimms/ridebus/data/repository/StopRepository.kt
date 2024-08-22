package org.xtimms.ridebus.data.repository

import kotlinx.coroutines.flow.Flow
import org.xtimms.ridebus.data.model.Stop

typealias Stops = List<Stop>

interface StopRepository {

    fun getStops(cityId: Int): Flow<Stops>
    suspend fun getStop(stopId: Int): Stop?
}
