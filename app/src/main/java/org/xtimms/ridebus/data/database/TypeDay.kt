package org.xtimms.ridebus.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "typeDay")
data class TypeDay(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "_id")
    var id: Int,
    @ColumnInfo(name = "typeDay") var typeDay: String
)
