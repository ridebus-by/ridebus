package org.xtimms.ridebus.data.database

import androidx.room.*

@Entity(
    tableName = "route",
    foreignKeys = [
        ForeignKey(
            entity = City::class,
            parentColumns = arrayOf("_id"),
            childColumns = arrayOf("city_id")
        ), ForeignKey(
            entity = Transport::class,
            parentColumns = arrayOf("_id"),
            childColumns = arrayOf("transport_id")
        ), ForeignKey(
            entity = KindRoute::class,
            parentColumns = arrayOf("_id"),
            childColumns = arrayOf("kindRoute_id")
        )
    ],
    indices = [Index("city_id"), Index("transport_id"), Index("kindRoute_id")]
)
data class Route(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "_id")
    var id: Int,
    @ColumnInfo(name = "city_id") var cityId: Int,
    @ColumnInfo(name = "transport_id") var transportId: Int,
    @ColumnInfo(name = "kindRoute_id") var kindRouteId: Int,
    @ColumnInfo(name = "routeNumber") var number: String?,
    @ColumnInfo(name = "routeTitle") var title: String
)
