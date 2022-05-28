package org.xtimms.ridebus.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "city")
data class City(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "_id")
    val cityId: Int,
    @ColumnInfo(name = "cityName") val city: String,
)
