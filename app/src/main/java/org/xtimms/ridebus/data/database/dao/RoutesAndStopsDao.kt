package org.xtimms.ridebus.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import org.xtimms.ridebus.data.database.entity.Coordinates
import org.xtimms.ridebus.data.database.entity.Route
import org.xtimms.ridebus.data.database.entity.Stop

@Dao
interface RoutesAndStopsDao {

    @Query("SELECT route.* FROM routeStops INNER JOIN route on route._id = routeStops.route_id WHERE stop_id = :stopId")
    fun getRoutesByStop(stopId: Int): List<Route>

    @Query("SELECT stop.* FROM routeStops INNER JOIN stop ON stop._id = routeStops.stop_id WHERE routeStops.route_id = :routeId")
    fun getStopsByRoute(routeId: Int): List<Stop>

    @Query("SELECT stop.latitude, stop.longitude FROM routeStops INNER JOIN stop ON stop._id = routeStops.stop_id WHERE routeStops.route_id = :routeId")
    fun getStopsCoordinates(routeId: Int): List<Coordinates>
}
