package org.xtimms.ridebus.util.system

import android.content.Context
import androidx.annotation.Nullable
import org.xtimms.ridebus.R
import java.text.ParseException
import java.util.*

class Time private constructor(calendar: Calendar) {

    private val date: Date = calendar.time

    fun isAfter(time: Time): Boolean {
        return date.after(time.date)
    }

    val isWeekend: Boolean
        get() {
            val dayOfWeek: Int = buildCalendar(date).get(Calendar.DAY_OF_WEEK)
            return dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY
        }

    fun toRelativeString(context: Context): String {
        val currentTime = current()
        return if (date == currentTime.date) {
            context.getString(R.string.token_time_now)
        } else Formatters.getRelativeTimeFormatter().setReference(currentTime.date).format(date)
    }

    fun toSystemString(): String {
        return Formatters.getSystemTimeFormatter().format(date)
    }

    companion object {

        fun from(@Nullable databaseTimeString: String): Time {
            return Time(buildCalendar(buildDate(databaseTimeString)))
        }

        private fun buildCalendar(date: Date): Calendar {
            val calendar: Calendar = Calendar.getInstance()
            calendar.time = date
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            return calendar
        }

        private fun buildDate(databaseTimeString: String): Date {
            return if (databaseTimeString.isEmpty()) {
                Date(0)
            } else try {
                Formatters.getDatabaseTimeFormatter().parse(databaseTimeString)
            } catch (e: ParseException) {
                Date(0)
            }
        }

        fun current(): Time {
            val calendar: Calendar = Calendar.getInstance()
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            return Time(calendar)
        }
    }
}
