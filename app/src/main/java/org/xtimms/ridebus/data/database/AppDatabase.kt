package org.xtimms.ridebus.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*

@Database(
    entities = [
        City::class,
        Transport::class,
        TypeDay::class,
        KindRoute::class,
        Route::class,
        Stop::class,
        RouteStops::class,
        Schedule::class,
        ClassTransport::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun dao(): Dao

    companion object {

        const val DATABASE_NAME = "ridebus.db"

        fun copyDatabase(context: Context, databaseName: String?) {
            val dbPath = context.getDatabasePath(databaseName)

            if (dbPath.exists()) {
                context.deleteDatabase(DATABASE_NAME)
            }

            val s = Objects.requireNonNull(dbPath.parentFile).mkdirs()

            try {
                val inputStream = context.assets.open(databaseName!!)
                val output: OutputStream = FileOutputStream(dbPath)
                val buffer = ByteArray(8192)
                var length: Int
                while (inputStream.read(buffer, 0, 8192).also { length = it } > 0) {
                    output.write(buffer, 0, length)
                }
                output.flush()
                output.close()
                inputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}
