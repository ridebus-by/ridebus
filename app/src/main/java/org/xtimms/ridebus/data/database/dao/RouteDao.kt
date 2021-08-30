package org.xtimms.ridebus.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import org.xtimms.ridebus.data.database.entity.Route

//
// Created by Xtimms on 28.08.2021.
//
@Dao
interface RouteDao {
    @Query("SELECT * FROM Routes")
    fun getAll(): List<Route>
}
