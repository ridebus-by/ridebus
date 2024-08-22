package org.xtimms.ridebus.data.model

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Route(
    var routeId: Int = 0,
    val cityId: Int = 0,
    val transportId: Int = 0,
    val kindId: Int = 0,
    val number: String = "",
    val title: String = "",
    val description: String = "",
    val fare: String = "",
    val weeklyTraffic: String = "",
    val workingHours: String = "",
    val following: String = "",
    val carrierCompany: String = "",
    val techInfo: String = "",
    val cash: Boolean = false,
    val qrCode: Boolean = false,
    val isSmall: Boolean = false,
    val isBig: Boolean = false,
    val isVeryBig: Boolean = false,
    val isEco: Boolean = false,
    val wifi: Boolean = false,
    val isLowFloor: Boolean = false
)
