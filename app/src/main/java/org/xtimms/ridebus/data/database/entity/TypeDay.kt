package org.xtimms.ridebus.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "typeDay")
data class TypeDay(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "_id")
    val transportId: Int,
    @ColumnInfo(name = "typeDay") val typeDay: String,
)
