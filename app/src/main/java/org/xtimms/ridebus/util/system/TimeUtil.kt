package org.xtimms.ridebus.util.system

import org.xtimms.ridebus.util.TypeOfDay
import org.xtimms.ridebus.util.lang.*
import java.text.SimpleDateFormat
import java.util.*

object TimeUtil {

    fun getTypeDay(currentDate: String): Int {
        val formatter = SimpleDateFormat("EEEE", Locale.getDefault())
        val date = formatter.parse(currentDate) ?: Calendar.getInstance().time
        val calendar: Calendar = Calendar.getInstance()
        calendar.time = date
        val typeDay = if (date.isWeekend) {
            TypeOfDay.WEEKEND.idInDatabase
        } else if (date.isWeekday) {
            TypeOfDay.WEEKDAYS.idInDatabase
        } else if (date.isEveryday) {
            TypeOfDay.EVERYDAY.idInDatabase
        } else {
            TypeOfDay.ON_FRIDAY.idInDatabase
        }
        return typeDay
    }
}
