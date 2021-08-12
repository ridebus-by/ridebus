package org.xtimms.ridebus.data.database

import android.provider.BaseColumns

object DatabaseSchema {
    const val DATABASE_NAME = "ridebus.db"

    object Versions {
        const val CURRENT = 2
        const val INITIAL = 1
    }

    object Aliases {
        const val DATABASE = "db"
    }

    object Tables {
        const val ROUTES = "Routes"
        const val TRIP_TYPES = "TripTypes"
        const val TRIPS = "Trips"
        const val STOPS = "Stops"
        const val ROUTES_AND_STOPS = "RoutesAndStops"
    }

    object RoutesColumns : BaseColumns {
        const val NUMBER = "number"
        const val DESCRIPTION = "description"
    }

    object TripTypesColumns : BaseColumns {
        const val NAME = "name"
    }

    object TripTypesColumnsValues {
        const val FULL_WEEK_ID = 0
        const val WORKDAY_ID = 1
        const val WEEKEND_ID = 2
    }

    object TripsColumns : BaseColumns {
        const val TYPE_ID = "type_id"
        const val ROUTE_ID = "route_id"
        const val HOUR = "hour"
        const val MINUTE = "minute"
    }

    object StopsColumns : BaseColumns {
        const val NAME = "name"
        const val DIRECTION = "direction"
        const val LONGITUDE = "longitude"
        const val LATITUDE = "latitude"
    }

    object RoutesAndStopsColumns : BaseColumns {
        const val ROUTE_ID = "route_id"
        const val STOP_ID = "stop_id"
        const val SHIFT_HOUR = "shift_hour"
        const val SHIFT_MINUTE = "shift_minute"
    }
}
