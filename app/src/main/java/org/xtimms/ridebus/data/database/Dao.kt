package org.xtimms.ridebus.data.database

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface Dao {
    @Query("SELECT * FROM stop ORDER BY stopTitle ASC")
    fun getStops(): List<Stop>

    @Query("SELECT * FROM route WHERE (city_id=1) ORDER BY LENGTH(routeNumber), routeNumber")
    fun getRoutes(): List<Route>

    @Query("SELECT * FROM route WHERE (city_id=1 AND transport_id=1) ORDER BY LENGTH(routeNumber), routeNumber")
    fun getBuses(): List<Route>

    @Query("SELECT * FROM route WHERE (city_id=1 AND transport_id=3) ORDER BY LENGTH(routeNumber), routeNumber")
    fun getExpresses(): List<Route>

    @Query("SELECT * FROM route WHERE (city_id=1 AND transport_id=2) ORDER BY LENGTH(routeNumber), routeNumber")
    fun getTrams(): List<Route>

    @Query("SELECT * FROM route WHERE (city_id=1 AND transport_id=4) ORDER BY LENGTH(routeNumber), routeNumber")
    fun getRouteTaxis(): List<Route>

    @Query("SELECT route.* FROM routeStops" +
            " INNER JOIN route on route._id = routeStops.route_id WHERE stop_id = :stopId")
    fun getRoutesByStop(stopId: Int): List<Route>

    @Query("SELECT stop.* FROM routeStops INNER JOIN stop ON stop._id = routeStops.stop_id " +
            "WHERE routeStops.route_id = :routeId ORDER BY stopNumber ASC")
    fun getStops(routeId: Int): List<Stop>

    @Query("SELECT schedule.time FROM routeStops INNER JOIN schedule " +
            "ON (schedule.routeStop_id = routeStops._id and schedule.typeDay_id = :typeDay) " +
            "WHERE (routeStops.route_id = :routeId and routeStops.stop_id = :stop_id)")
    fun getTimeOnStop(typeDay: Int, routeId: Int, stop_id: Int): List<String>

    @Query("SELECT schedule.typeDay_id FROM schedule WHERE schedule.routeStop_id = " +
            "(SELECT routeStops._id FROM routeStops WHERE route_id = :routeId AND stop_id = :stop_id)" +
            " GROUP BY schedule.typeDay_id")
    fun getTypeDay(routeId: Int, stop_id: Int): List<Int>
}