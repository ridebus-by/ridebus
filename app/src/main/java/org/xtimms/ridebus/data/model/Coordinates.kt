package org.xtimms.ridebus.data.model

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Coordinates(
    val latitude: String = "",
    val longitude: String = ""
)
