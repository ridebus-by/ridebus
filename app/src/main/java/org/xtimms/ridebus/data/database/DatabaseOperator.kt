package org.xtimms.ridebus.data.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.annotation.NonNull
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.lang.NullPointerException
import java.lang.RuntimeException

class DatabaseOperator(context: Context) {

    private val context: Context = context.applicationContext

    fun databaseExists(): Boolean {
        return databaseFile.exists()
    }

    private val databaseFile: File = context.getDatabasePath(DatabaseSchema.DATABASE_NAME).absoluteFile

    val databasePath: String = databaseFile.path

    fun replaceDatabaseFile(@NonNull databaseContents: InputStream) {
        try {
            val tempDatabaseFile: File = getTempFile(databaseContents)
            val databaseFile: File = databaseFile
            FileUtils.copyFile(tempDatabaseFile, databaseFile)
            tempDatabaseFile.delete()
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    private fun getTempFile(fileContents: InputStream): File {
        return try {
            val tempFile: File = File.createTempFile("ridebus", null, context.cacheDir)
            FileUtils.copyInputStreamToFile(fileContents, tempFile)
            tempFile
        } catch (e: IOException) {
            throw RuntimeException(e)
        } catch (e: NullPointerException) {
            throw RuntimeException(e)
        }
    }

    fun replaceDatabaseContents(@NonNull databaseContents: InputStream) {
        val database: SQLiteDatabase = DatabaseOpenHelper(context).writableDatabase
        val tempDatabaseFile: File = getTempFile(databaseContents)
        deleteDatabaseContents(database)
        insertDatabaseContents(database, tempDatabaseFile)
        database.close()
        tempDatabaseFile.delete()
    }

    private fun deleteDatabaseContents(database: SQLiteDatabase) {
        try {
            database.beginTransaction()
            database.execSQL(SqlBuilder.buildDeleteClause(DatabaseSchema.Tables.ROUTES_AND_STOPS))
            database.execSQL(SqlBuilder.buildDeleteClause(DatabaseSchema.Tables.TRIPS))
            database.execSQL(SqlBuilder.buildDeleteClause(DatabaseSchema.Tables.TRIP_TYPES))
            database.execSQL(SqlBuilder.buildDeleteClause(DatabaseSchema.Tables.ROUTES))
            database.execSQL(SqlBuilder.buildDeleteClause(DatabaseSchema.Tables.STOPS))
            database.setTransactionSuccessful()
        } finally {
            database.endTransaction()
        }
    }

    private fun insertDatabaseContents(database: SQLiteDatabase, databaseContentsFile: File) {
        database.execSQL(
            SqlBuilder.buildAttachClause(
                databaseContentsFile.absolutePath,
                DatabaseSchema.Aliases.DATABASE
            )
        )
        try {
            database.beginTransaction()
            database.execSQL(
                SqlBuilder.buildInsertClause(
                    DatabaseSchema.Tables.ROUTES,
                    DatabaseSchema.Aliases.DATABASE
                )
            )
            database.execSQL(
                SqlBuilder.buildInsertClause(
                    DatabaseSchema.Tables.TRIP_TYPES,
                    DatabaseSchema.Aliases.DATABASE
                )
            )
            database.execSQL(
                SqlBuilder.buildInsertClause(
                    DatabaseSchema.Tables.TRIPS,
                    DatabaseSchema.Aliases.DATABASE
                )
            )
            database.execSQL(
                SqlBuilder.buildInsertClause(
                    DatabaseSchema.Tables.STOPS,
                    DatabaseSchema.Aliases.DATABASE
                )
            )
            database.execSQL(
                SqlBuilder.buildInsertClause(
                    DatabaseSchema.Tables.ROUTES_AND_STOPS,
                    DatabaseSchema.Aliases.DATABASE
                )
            )
            database.setTransactionSuccessful()
        } finally {
            database.endTransaction()
        }
        database.execSQL(SqlBuilder.buildDetachClause(DatabaseSchema.Aliases.DATABASE))
    }

    companion object {
        fun with(@NonNull context: Context): DatabaseOperator {
            return DatabaseOperator(context)
        }
    }
}
