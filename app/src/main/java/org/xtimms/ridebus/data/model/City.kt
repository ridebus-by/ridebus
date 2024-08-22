package org.xtimms.ridebus.data.model

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class City(
    val cityId: Int = 0,
    val city: String = "",
    val latitude: String = "",
    val longitude: String = ""
)
