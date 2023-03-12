package org.xtimms.ridebus.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import org.xtimms.ridebus.data.database.entity.Coordinates
import org.xtimms.ridebus.data.database.entity.Stop

//
// Created by Xtimms on 01.09.2021.
//
@Dao
interface StopDao {

    @Query("SELECT * FROM stop WHERE city_id = :cityId ORDER BY name ASC")
    fun getAllStops(cityId: Int): List<Stop>

    @Query("SELECT * FROM stop WHERE _id = :stopId")
    fun getStop(stopId: Int): List<Stop>

    @Query("SELECT latitude, longitude FROM stop WHERE city_id = :cityId")
    fun getCoordinates(cityId: Int): List<Coordinates>
}
