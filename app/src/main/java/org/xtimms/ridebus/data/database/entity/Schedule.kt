package org.xtimms.ridebus.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "schedule")
data class Schedule(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "_id")
    val id: Int,
    @ColumnInfo(name = "route_id") val routeId: Int,
    @ColumnInfo(name = "type_day") val typeDay: Int,
    @ColumnInfo(name = "hour") val hour: Int,
    @ColumnInfo(name = "minute") val minute: Int
)
