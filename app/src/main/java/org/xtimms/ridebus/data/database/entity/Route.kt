package org.xtimms.ridebus.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

//
// Created by Xtimms on 28.08.2021.
//
@Entity(tableName = "route")
data class Route(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "_id")
    val routeId: Int,
    @ColumnInfo(name = "city_id") val cityId: Int,
    @ColumnInfo(name = "transport_id") val transportId: Int,
    @ColumnInfo(name = "kindRoute_id") val kindId: Int,
    @ColumnInfo(name = "route_number") val number: String,
    @ColumnInfo(name = "route_title") val title: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "fare") val fare: String,
    @ColumnInfo(name = "weekly_traffic") val weeklyTraffic: String,
    @ColumnInfo(name = "working_hours") val workingHours: String,
    @ColumnInfo(name = "following") val following: String,
    @ColumnInfo(name = "carrier_company") val carrierCompany: String,
    @ColumnInfo(name = "tech_info") val techInfo: String,
    @ColumnInfo(name = "cash") val cash: Int,
    @ColumnInfo(name = "qr_code") val qrCode: Int,
    @ColumnInfo(name = "is_small") val isSmall: Int,
    @ColumnInfo(name = "is_big") val isBig: Int,
    @ColumnInfo(name = "is_very_big") val isVeryBig: Int,
)
