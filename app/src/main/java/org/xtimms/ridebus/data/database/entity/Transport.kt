package org.xtimms.ridebus.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transport")
data class Transport(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "_id")
    val transportId: Int,
    @ColumnInfo(name = "type") val type: String
)
