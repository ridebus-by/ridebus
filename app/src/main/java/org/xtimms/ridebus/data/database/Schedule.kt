package org.xtimms.ridebus.data.database

import androidx.room.*

@Entity(
    tableName = "schedule",
    foreignKeys = [ForeignKey(
        entity = RouteStop::class,
        parentColumns = arrayOf("_id"),
        childColumns = arrayOf("routeStop_id")
    ), ForeignKey(entity = TypeDay::class,
        parentColumns = arrayOf("_id"),
        childColumns = arrayOf("typeDay_id")
    )],
    indices = [Index("typeDay_id"), Index("routeStop_id")]
)
data class Schedule(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "_id") var id: Int,
    @ColumnInfo(name = "routeStop_id") var routeStopId: Int,
    @ColumnInfo(name = "typeDay_id") var typeDayId: Int,
    @ColumnInfo(name = "time") var time: String
)