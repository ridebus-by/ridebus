package org.xtimms.ridebus.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "schedule")
data class Schedule(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "_id")
    val id: Int,
    @ColumnInfo(name = "routeStop_id") val routeStopId: Int,
    @ColumnInfo(name = "typeDay_id") val typeDayId: Int,
    @ColumnInfo(name = "time") val time: String
)
