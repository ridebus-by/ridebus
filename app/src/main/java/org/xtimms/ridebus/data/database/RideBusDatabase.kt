package org.xtimms.ridebus.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import org.xtimms.ridebus.data.database.dao.RouteDao
import org.xtimms.ridebus.data.database.entity.RouteStop

//
// Created by Xtimms on 28.08.2021.
//
@Database(entities = arrayOf(RouteStop::class), version = 1)
abstract class RideBusDatabase : RoomDatabase() {

    private val DB_NAME = "ridebus"
    private var instance: RideBusDatabase? = null

    @Synchronized
    open fun getInstance(context: Context): RideBusDatabase? {
        if (instance == null) {
            instance = Room.databaseBuilder(
                context.applicationContext,
                RideBusDatabase::class.java, DB_NAME
            )
                .fallbackToDestructiveMigration()
                .build()
        }
        return instance
    }

    abstract fun routeDao(): RouteDao
}
