package org.xtimms.ridebus.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import org.xtimms.ridebus.data.database.entity.Stop

@Dao
interface RoutesAndStopsDao {
    @Query("SELECT Stops.* FROM RoutesAndStops INNER JOIN Stops ON Stops._id = RoutesAndStops.stop_id WHERE RoutesAndStops.route_id = :routeId")
    fun getStopsByRoute(routeId: Int): List<Stop>
}
