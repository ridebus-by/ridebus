package org.xtimms.ridebus.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "kindRoute")
data class KindOfRoute(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "_id")
    val kindId: Int,
    @ColumnInfo(name = "kindRoute") val kindRoute: String
)
