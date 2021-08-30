package org.xtimms.ridebus.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

//
// Created by Xtimms on 28.08.2021.
//
@Entity(tableName = "RoutesAndStops")
data class RouteStop(
    @PrimaryKey @ColumnInfo(name = "route_id")
    val routeId: Int,
    @ColumnInfo(name = "stop_id") val stopId: Int,
    @ColumnInfo(name = "shift_hour") val shiftHour: Int,
    @ColumnInfo(name = "shift_minute") val shiftMinute: Int
)
