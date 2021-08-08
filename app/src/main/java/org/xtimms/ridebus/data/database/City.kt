package org.xtimms.ridebus.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "city")
data class City(
    @field:PrimaryKey(autoGenerate = true) @field:ColumnInfo(name = "_id")
    var id: Int,
    @field:ColumnInfo(name = "cityName") var title: String?
)
