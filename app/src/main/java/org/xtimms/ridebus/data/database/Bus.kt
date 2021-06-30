package org.xtimms.ridebus.data.database

sealed class Bus {

    abstract val number: String
    abstract val name: String

}