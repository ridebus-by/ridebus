package org.xtimms.ridebus.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import org.xtimms.ridebus.data.database.dao.*
import org.xtimms.ridebus.data.database.entity.*

//
// Created by Xtimms on 28.08.2021.
//
@Database(entities = [Route::class, Stop::class, RoutesAndStops::class, City::class, KindOfRoute::class, Transport::class, Schedule::class, TypeDay::class], version = 1)
abstract class RideBusDatabase : RoomDatabase() {

    abstract fun routeDao(): RouteDao

    abstract fun stopDao(): StopDao

    abstract fun routesAndStopsDao(): RoutesAndStopsDao

    abstract fun scheduleDao(): ScheduleDao

    companion object {
        @Volatile
        private var INSTANCE: RideBusDatabase? = null

        fun getDatabase(context: Context): RideBusDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    RideBusDatabase::class.java,
                    "ridebus.db"
                )
                    .allowMainThreadQueries()
                    .createFromAsset("database/ridebus.db")
                    .build()
                INSTANCE = instance

                instance
            }
        }
    }
}
