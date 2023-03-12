package org.xtimms.ridebus.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import org.xtimms.ridebus.data.database.entity.City
import org.xtimms.ridebus.data.database.entity.Coordinates

@Dao
interface CityDao {

    @Query("SELECT * FROM city")
    fun getCities(): List<City>

    @Query("SELECT _id FROM city")
    fun getCitiesIds(): List<Int>

    @Query("SELECT cityName FROM city")
    fun getCitiesNames(): List<String>

    @Query("SELECT latitude, longitude FROM city WHERE _id = :cityId")
    fun getCityCoordinates(cityId: Int): List<Coordinates>
}
