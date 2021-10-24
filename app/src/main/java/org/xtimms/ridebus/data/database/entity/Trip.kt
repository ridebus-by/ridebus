package org.xtimms.ridebus.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Trips")
data class Trip(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "_id")
    val tripId: Int,
    @ColumnInfo(name = "type_id") val typeId: Int,
    @ColumnInfo(name = "route_id") val routeId: Int,
    @ColumnInfo(name = "hour") val hour: Int,
    @ColumnInfo(name = "minute") val minute: Int
)
