package org.xtimms.ridebus.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import org.xtimms.ridebus.data.database.entity.RouteStop

//
// Created by Xtimms on 28.08.2021.
//
@Dao
interface RouteStopDao {
    @Query("SELECT * FROM RoutesAndStops")
    fun getAll(): List<RouteStop>
}
