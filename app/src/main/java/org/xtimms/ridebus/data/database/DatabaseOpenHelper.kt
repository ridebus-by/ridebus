package org.xtimms.ridebus.data.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.annotation.NonNull
import org.xtimms.ridebus.util.Assets

class DatabaseOpenHelper(@NonNull context: Context) :
    SQLiteOpenHelper(context, DatabaseSchema.DATABASE_NAME, null, DatabaseSchema.Versions.CURRENT) {

    private val context: Context = context.applicationContext

    private var database: SQLiteDatabase? = null

    override fun onCreate(database: SQLiteDatabase?) = Unit

    override fun onUpgrade(database: SQLiteDatabase?, oldDatabaseVersion: Int, newDatabaseVersion: Int) = Unit

    override fun getReadableDatabase(): SQLiteDatabase? {
        if (databaseAvailable()) {
            return database
        }
        if (!databaseExists()) {
            createDatabase()
            changeDatabaseVersion()
        }
        if (!databaseHasCurrentVersion()) {
            createDatabase()
            changeDatabaseVersion()
        }
        database = openReadableDatabase()
        return database
    }

    private fun databaseAvailable(): Boolean {
        return database != null && database!!.isOpen
    }

    private fun databaseExists(): Boolean {
        return DatabaseOperator.with(context).databaseExists()
    }

    private fun createDatabase() {
        DatabaseOperator.with(context).replaceDatabaseFile(Assets.of(context).databaseContents)
    }

    private fun changeDatabaseVersion() {
        val database: SQLiteDatabase = openWritableDatabase()
        database.version = DatabaseSchema.Versions.CURRENT
        database.close()
    }

    private fun openWritableDatabase(): SQLiteDatabase {
        return SQLiteDatabase.openDatabase(databasePath, null, SQLiteDatabase.OPEN_READWRITE)
    }

    private val databasePath: String = DatabaseOperator.with(context).databasePath

    private fun databaseHasCurrentVersion(): Boolean {
        val database: SQLiteDatabase = openReadableDatabase()
        val databaseVersion: Int = database.version
        database.close()
        return databaseVersion == DatabaseSchema.Versions.CURRENT
    }

    private fun openReadableDatabase(): SQLiteDatabase {
        return SQLiteDatabase.openDatabase(databasePath, null, SQLiteDatabase.OPEN_READONLY)
    }
}
