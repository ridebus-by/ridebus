package org.xtimms.ridebus.data.database

import androidx.room.*

@Entity(
    tableName = "routeStops",
    foreignKeys = [
        ForeignKey(
            entity = Route::class,
            parentColumns = arrayOf("_id"),
            childColumns = arrayOf("route_id")
        ), ForeignKey(
            entity = Stop::class,
            parentColumns = arrayOf("_id"),
            childColumns = arrayOf("stop_id")
        )
    ],
    indices = [Index("route_id"), Index("stop_id")]
)
class RouteStops(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "_id")
    var id: Int,
    @ColumnInfo(name = "route_id") var routeId: Int,
    @ColumnInfo(name = "stop_id") var stopId: Int,
    @ColumnInfo(name = "stopNumber") var stopNumber: Int
)
