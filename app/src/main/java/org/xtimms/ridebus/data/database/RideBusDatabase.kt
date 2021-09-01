package org.xtimms.ridebus.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import org.xtimms.ridebus.data.database.dao.*
import org.xtimms.ridebus.data.database.entity.*
import java.io.*

//
// Created by Xtimms on 28.08.2021.
//
@Database(entities = [Route::class], version = 1)
abstract class RideBusDatabase : RoomDatabase() {

    abstract fun routeDao(): RouteDao

    companion object {
        @Volatile
        private var INSTANCE: RideBusDatabase? = null

        fun getDatabase(context: Context): RideBusDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    RideBusDatabase::class.java,
                    "ridebus-db"
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
