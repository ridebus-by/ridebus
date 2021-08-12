package org.xtimms.ridebus.util

import android.content.Context
import androidx.annotation.NonNull
import org.xtimms.ridebus.data.database.DatabaseSchema
import java.io.IOException
import java.io.InputStream
import java.lang.RuntimeException

class Assets private constructor(context: Context) {
    private val context: Context = context.applicationContext

    val databaseContents: InputStream
        get() = try {
            context.assets.open(DatabaseSchema.DATABASE_NAME)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }

    companion object {
        fun of(@NonNull context: Context): Assets {
            return Assets(context)
        }
    }
}
