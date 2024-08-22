package org.xtimms.ridebus.data.repository

import org.xtimms.ridebus.data.model.City
import org.xtimms.ridebus.data.model.Coordinates

interface CityRepository {

    suspend fun getCities(): List<City>

    suspend fun getCitiesIds(): List<Int>

    suspend fun getCitiesNames(): List<String>

    suspend fun getCityCoordinates(cityId: Int): List<Coordinates>
}
