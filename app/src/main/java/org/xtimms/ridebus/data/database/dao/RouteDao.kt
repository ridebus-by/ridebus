package org.xtimms.ridebus.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import org.xtimms.ridebus.data.database.entity.Route

//
// Created by Xtimms on 28.08.2021.
//
@Dao
interface RouteDao {
    @Query("SELECT * FROM Routes WHERE (transport_id = 1 AND city_id = :cityId)")
    fun getBuses(cityId: Int): List<Route>

    @Query("SELECT * FROM Routes WHERE (transport_id = 2 AND city_id = :cityId)")
    fun getTaxis(cityId: Int): List<Route>

    @Query("SELECT * FROM Routes WHERE (transport_id = 3 AND city_id = :cityId)")
    fun getExpresses(cityId: Int): List<Route>
}
