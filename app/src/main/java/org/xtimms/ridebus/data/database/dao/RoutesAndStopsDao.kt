package org.xtimms.ridebus.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import org.xtimms.ridebus.data.database.entity.Route
import org.xtimms.ridebus.data.database.entity.Stop

@Dao
interface RoutesAndStopsDao {

    @Query("SELECT Routes.* FROM RoutesAndStops INNER JOIN Routes on Routes._id = RoutesAndStops.route_id WHERE stop_id = :stopId")
    fun getRoutesByStop(stopId: Int): List<Route>

    @Query("SELECT Stops.* FROM RoutesAndStops INNER JOIN Stops ON Stops._id = RoutesAndStops.stop_id WHERE RoutesAndStops.route_id = :routeId")
    fun getStopsByRoute(routeId: Int): List<Stop>

    @Query("SELECT strftime('%Y-%m-%d %H:%M', 'now', 'localtime', 'start of day', Trips.hour || ' hours', Trips.minute || ' minutes', RoutesAndStops.shift_hour || ' hours', RoutesAndStops.shift_minute || ' minutes') AS arrival_time FROM Trips, RoutesAndStops, Routes WHERE Trips.route_id = Routes._id AND Trips.type_id IN (0, 1) AND arrival_time >= strftime('%Y-%m-%d %H:%M', 'now', 'localtime') ORDER BY Trips.hour, Trips.minute LIMIT 1")
    fun getArrivalTime(): String
}
