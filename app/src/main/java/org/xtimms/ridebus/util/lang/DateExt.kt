package org.xtimms.ridebus.util.lang

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

fun Date.toDateTimestampString(dateFormatter: DateFormat): String {
    val date = dateFormatter.format(this)
    val time = DateFormat.getTimeInstance(DateFormat.SHORT).format(this)
    return "$date $time"
}

fun String.getDayOfWeek(format: String): Int {
    val date = SimpleDateFormat(format, Locale.getDefault()).parse(this)
    return date?.getDayOfWeek() ?: 0
}

fun Date.getDayOfWeek(): Int {
    val c = Calendar.getInstance().apply { time = this@getDayOfWeek }
    return c[Calendar.DAY_OF_WEEK] - 1
}

val Date.isMonday: Boolean
    get() {
        val calendar = Calendar.getInstance()
        calendar.time = this
        return calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY
    }

val Date.isTuesday: Boolean
    get() {
        val calendar = Calendar.getInstance()
        calendar.time = this
        return calendar.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY
    }

val Date.isWednesday: Boolean
    get() {
        val calendar = Calendar.getInstance()
        calendar.time = this
        return calendar.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY
    }

val Date.isThursday: Boolean
    get() {
        val calendar = Calendar.getInstance()
        calendar.time = this
        return calendar.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY
    }

val Date.isFriday: Boolean
    get() {
        val calendar = Calendar.getInstance()
        calendar.time = this
        return calendar.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY
    }

val Date.isSaturday: Boolean
    get() {
        val calendar = Calendar.getInstance()
        calendar.time = this
        return calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
    }

val Date.isSunday: Boolean
    get() {
        val calendar = Calendar.getInstance()
        calendar.time = this
        return calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
    }

val Date.isWeekend: Boolean
    get() = this.isSaturday || this.isSunday

val Date.isWeekday: Boolean
    get() = !this.isWeekend

val Date.isEveryday: Boolean
    get() = this.isWeekday || this.isWeekend
