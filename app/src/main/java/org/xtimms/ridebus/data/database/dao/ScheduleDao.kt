package org.xtimms.ridebus.data.database.dao

import androidx.room.Dao
import androidx.room.Query

@Dao
interface ScheduleDao {

    @Query("SELECT schedule.time FROM routeStops INNER JOIN schedule ON (schedule.routeStop_id = routeStops._id AND schedule.typeDay_id = :typeDay) WHERE (routeStops.route_id = :routeId AND routeStops.stop_id = :stopId)")
    fun getArrivalTimeOnStop(typeDay: Int, routeId: Int, stopId: Int): List<String>

    @Query("SELECT schedule.typeDay_id FROM schedule WHERE schedule.routeStop_id = (SELECT routeStops._id FROM routeStops WHERE route_id = :routeId AND stop_id = :stopId) GROUP BY schedule.typeDay_id")
    fun getTypeDay(routeId: Int, stopId: Int): List<Int>

}
