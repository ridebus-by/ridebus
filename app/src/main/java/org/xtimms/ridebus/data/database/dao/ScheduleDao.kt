package org.xtimms.ridebus.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleDao {

    @Query("SELECT schedule._id AS id, schedule.route_id AS routeId, schedule.type_day AS typeDay, strftime('%H:%M', 'now', 'localtime', 'start of day', schedule.hour || ' hours', schedule.minute || ' minutes', routeStops.shift_hour || ' hours', routeStops.shift_minute || ' minutes') AS arrivalTime FROM schedule, routeStops WHERE (routeStops.route_id = :routeId AND schedule.route_id = :routeId AND routeStops.stop_id = :stopId AND schedule.type_day = :typeDay) ORDER BY schedule.hour, schedule.minute")
    fun getArrivalTime(typeDay: Int, routeId: Int, stopId: Int): List<Timetable>

    @Transaction
    @Query("SELECT schedule._id AS id, schedule.route_id AS routeId, schedule.type_day AS typeDay, strftime('%H:%M', 'now', 'localtime', 'start of day', schedule.hour || ' hours', schedule.minute || ' minutes', routeStops.shift_hour || ' hours', routeStops.shift_minute || ' minutes') AS arrivalTime FROM schedule, routeStops WHERE (routeStops.route_id = :routeId AND schedule.route_id = :routeId AND routeStops.stop_id = :stopId AND schedule.type_day = :typeDay) ORDER BY schedule.hour, schedule.minute ASC")
    fun observeArrivalTime(typeDay: Int, routeId: Int, stopId: Int): Flow<List<Timetable>>

    @Query("SELECT DISTINCT hour FROM schedule where route_id = :routeId and type_day = :typeDay")
    fun getHours(typeDay: Int, routeId: Int): List<Int>

    @Query("SELECT strftime('%H:%M', 'now', 'localtime', 'start of day', schedule.hour || ' hours', schedule.minute || ' minutes') as time FROM schedule where route_id = :routeId and type_day = :typeDay and hour = :hour")
    fun getMinutesInHour(typeDay: Int, routeId: Int, hour: Int): List<String>

    // Abandoned since 3.0 version of database
    /*@Query("SELECT * FROM routeStops INNER JOIN schedule ON (schedule.routeStop_id = routeStops._id AND schedule.typeDay_id = :typeDay) WHERE (routeStops.route_id = :routeId AND routeStops.stop_id = :stopId)")
    fun getArrivalTimeOnStop(typeDay: Int, routeId: Int, stopId: Int): List<Schedule>*/

    @Query("SELECT schedule.type_day FROM schedule, routeStops WHERE routeStops.stop_id = (SELECT routeStops._id FROM routeStops WHERE route_id = :routeId AND stop_id = :stopId) GROUP BY schedule.type_day")
    fun getTypeDay(routeId: Int, stopId: Int): List<Int>

    @Query("SELECT _id FROM typeDay WHERE EXISTS (SELECT * FROM schedule WHERE typeDay._id = schedule.type_day AND schedule.route_id = :routeId)")
    fun getTypesOfDay(routeId: Int): List<Int>

    data class Timetable(val id: Int?, val routeId: Int?, val typeDay: Int?, val arrivalTime: String?)
}
