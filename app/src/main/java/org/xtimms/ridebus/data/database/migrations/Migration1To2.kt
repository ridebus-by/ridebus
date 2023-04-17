package org.xtimms.ridebus.data.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration1To2 : Migration(1, 2) {

    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("DROP TABLE `route`")
        database.execSQL(
            "CREATE TABLE IF NOT EXISTS `route`" +
                " (`_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                " `city_id` INTEGER NOT NULL," +
                " `transport_id` INTEGER NOT NULL," +
                " `kindRoute_id` INTEGER NOT NULL," +
                " `route_number` TEXT NOT NULL," +
                " `route_title` TEXT NOT NULL," +
                " `description` TEXT NOT NULL," +
                " `fare` TEXT NOT NULL," +
                " `weekly_traffic` TEXT NOT NULL," +
                " `working_hours` TEXT NOT NULL," +
                " `following` TEXT NOT NULL," +
                " `carrier_company` TEXT NOT NULL," +
                " `tech_info` TEXT NOT NULL," +
                " `cash` INTEGER NOT NULL," +
                " `qr_code` INTEGER NOT NULL," +
                " `is_small` INTEGER NOT NULL," +
                " `is_big` INTEGER NOT NULL," +
                " `is_very_big` INTEGER NOT NULL," +
                " `eco` INTEGER NOT NULL," +
                " `wifi` INTEGER NOT NULL," +
                " `low_floor` INTEGER NOT NULL)"
        )
        database.execSQL("DROP TABLE `stop`")
        database.execSQL(
            "CREATE TABLE IF NOT EXISTS `stop`" +
                " (`_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                " `city_id` INTEGER NOT NULL," +
                " `transport_id` INTEGER NOT NULL," +
                " `kindRoute_id` INTEGER NOT NULL," +
                " `name` TEXT NOT NULL," +
                " `direction` TEXT NOT NULL," +
                " `latitude` TEXT NOT NULL," +
                " `longitude` TEXT NOT NULL)"
        )
        database.execSQL("DROP TABLE `routeStops`")
        database.execSQL(
            "CREATE TABLE IF NOT EXISTS `routeStops`" +
                " (`_id` INTEGER NOT NULL," +
                " `route_id` INTEGER NOT NULL," +
                " `stop_id` INTEGER NOT NULL," +
                " `shift_hour` INTEGER NOT NULL," +
                " `shift_minute` INTEGER NOT NULL," +
                " PRIMARY KEY(`_id`))"
        )
        database.execSQL("DROP TABLE `city`")
        database.execSQL(
            "CREATE TABLE IF NOT EXISTS `city`" +
                " (`_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                " `cityName` TEXT NOT NULL," +
                " `latitude` TEXT NOT NULL," +
                " `longitude` TEXT NOT NULL)"
        )
        database.execSQL("DROP TABLE `schedule`")
        database.execSQL(
            "CREATE TABLE IF NOT EXISTS `schedule`" +
                " (`_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                " `route_id` INTEGER NOT NULL," +
                " `type_day` INTEGER NOT NULL," +
                " `hour` INTEGER NOT NULL," +
                " `minute` INTEGER NOT NULL)"
        )
    }
}
