package org.xtimms.ridebus.data.model

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Stop(
    val stopId: Int = 0,
    val cityId: Int = 0,
    val transportId: Int = 0,
    val kindId: Int = 0,
    val name: String = "",
    val direction: String = "",
    val latitude: String = "",
    val longitude: String = ""
)
