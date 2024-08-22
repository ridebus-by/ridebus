package org.xtimms.ridebus.data.repository

interface TransportRepository {

    suspend fun getTypesOfTransportPerCity(cityId: Int): List<Int>
}
