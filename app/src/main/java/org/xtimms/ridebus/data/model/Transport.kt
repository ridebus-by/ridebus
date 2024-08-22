package org.xtimms.ridebus.data.model

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Transport(
    val transportId: Int = 0,
    val type: String = ""
)
