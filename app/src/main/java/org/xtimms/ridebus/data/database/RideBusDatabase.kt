package org.xtimms.ridebus.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import org.xtimms.ridebus.data.database.dao.CityDao
import org.xtimms.ridebus.data.database.dao.RouteDao
import org.xtimms.ridebus.data.database.dao.RoutesAndStopsDao
import org.xtimms.ridebus.data.database.dao.ScheduleDao
import org.xtimms.ridebus.data.database.dao.StopDao
import org.xtimms.ridebus.data.database.dao.TransportDao
import org.xtimms.ridebus.data.database.entity.City
import org.xtimms.ridebus.data.database.entity.KindOfRoute
import org.xtimms.ridebus.data.database.entity.Route
import org.xtimms.ridebus.data.database.entity.RoutesAndStops
import org.xtimms.ridebus.data.database.entity.Schedule
import org.xtimms.ridebus.data.database.entity.Stop
import org.xtimms.ridebus.data.database.entity.Transport
import org.xtimms.ridebus.data.database.entity.TypeDay
import org.xtimms.ridebus.data.database.migrations.Migration1To2

//
// Created by Xtimms on 28.08.2021.
//
@Database(
    entities = [
        Route::class,
        Stop::class,
        RoutesAndStops::class,
        City::class,
        KindOfRoute::class,
        Transport::class,
        Schedule::class,
        TypeDay::class
    ],
    version = 2
)
abstract class RideBusDatabase : RoomDatabase() {

    abstract fun routeDao(): RouteDao

    abstract fun stopDao(): StopDao

    abstract fun routesAndStopsDao(): RoutesAndStopsDao

    abstract fun scheduleDao(): ScheduleDao

    abstract fun transportDao(): TransportDao

    abstract fun cityDao(): CityDao

    companion object {
        @Volatile
        private var INSTANCE: RideBusDatabase? = null

        private val databaseMigrations: Array<Migration>
            get() = arrayOf(
                Migration1To2()
            )

        fun getDatabase(context: Context): RideBusDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    RideBusDatabase::class.java,
                    "ridebus.db"
                )
                    .addMigrations(*databaseMigrations)
                    .allowMainThreadQueries()
                    .createFromAsset("database/ridebus_v3.db").build()
                INSTANCE = instance

                instance
            }
        }
    }
}
