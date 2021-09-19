package org.xtimms.ridebus.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import org.xtimms.ridebus.data.database.entity.Stop

//
// Created by Xtimms on 01.09.2021.
//
@Dao
interface StopDao {
    @Query("SELECT * FROM Stops WHERE city_id = :cityId ORDER BY name ASC")
    fun getAll(cityId: Int): List<Stop>
}
