package org.xtimms.ridebus.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

//
// Created by Xtimms on 28.08.2021.
//
@Entity(tableName = "Routes")
data class Route(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "_id")
    val routeId: Int,
    @ColumnInfo(name = "city_id") val cityId: Int,
    @ColumnInfo(name = "transport_id") val transportId: Int,
    @ColumnInfo(name = "kind_id") val kindId: Int,
    @ColumnInfo(name = "number") val number: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "payment_methods") val paymentMethods: String,
    @ColumnInfo(name = "fare") val fare: String,
    @ColumnInfo(name = "weekly_traffic") val weeklyTraffic: String,
    @ColumnInfo(name = "working_hours") val workingHours: String,
    @ColumnInfo(name = "following") val following: String,
    @ColumnInfo(name = "carrier_company") val carrierCompany: String,
    @ColumnInfo(name = "tech_info") val techInfo: String
)
