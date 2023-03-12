package org.xtimms.ridebus.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import org.xtimms.ridebus.data.database.entity.Route

//
// Created by Xtimms on 28.08.2021.
//
@Dao
interface RouteDao {

    // column*1, column is for normal sorting with characters
    // like 1, 1a, 1b, 2, 2b, 3, 4, 5, 5a, 5b

    @Query("SELECT * FROM route WHERE (transport_id = :transportType AND city_id = :cityId) ORDER BY route_number*1, route_number")
    fun getRoutes(transportType: Int, cityId: Int): List<Route>

    @Query("SELECT * FROM route WHERE _id = :routeId")
    fun getRoute(routeId: Int): List<Route>
}
