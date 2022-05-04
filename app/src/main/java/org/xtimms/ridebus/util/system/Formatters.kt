package org.xtimms.ridebus.util.system

import android.content.Context
import org.ocpsoft.prettytime.PrettyTime
import org.ocpsoft.prettytime.units.JustNow
import org.ocpsoft.prettytime.units.Millisecond
import org.ocpsoft.prettytime.units.Second
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

object Formatters {

    private lateinit var databaseTimeFormatter: DateFormat
    private lateinit var relativeTimeFormatter: PrettyTime
    private lateinit var systemTimeFormatter: DateFormat

    fun getDatabaseTimeFormatter(): DateFormat {
        return databaseTimeFormatter
    }

    private fun buildDatabaseTimeFormatter(): DateFormat {
        return SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    }

    fun getRelativeTimeFormatter(): PrettyTime {
        return relativeTimeFormatter
    }

    private fun buildRelativeTimeFormatter(): PrettyTime {
        val formatter = PrettyTime()
        formatter.removeUnit(Second::class.java)
        formatter.removeUnit(Millisecond::class.java)
        formatter.removeUnit(JustNow::class.java)
        return formatter
    }

    fun getSystemTimeFormatter(): DateFormat {
        return systemTimeFormatter
    }

    private fun buildSystemTimeFormatter(context: Context): DateFormat? {
        return android.text.format.DateFormat.getTimeFormat(context)
    }
}
