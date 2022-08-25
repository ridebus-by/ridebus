package org.xtimms.ridebus.util.system

import org.xtimms.ridebus.util.TypeOfDay
import java.text.SimpleDateFormat
import java.util.*

object TimeUtil {

    fun getTypeDay(currentDate: String): Int {
        val formatter = SimpleDateFormat("EEEE", Locale.getDefault())
        val date = formatter.parse(currentDate) ?: Calendar.getInstance().time
        val calendar: Calendar = Calendar.getInstance()
        calendar.time = date
        val currentDay: Int = calendar.get(Calendar.DAY_OF_WEEK)
        val typeDay = if (currentDay == Calendar.SATURDAY && currentDay == Calendar.SUNDAY) {
            TypeOfDay.WEEKEND.idInDatabase
        } else {
            TypeOfDay.WEEKDAYS.idInDatabase
        }
        return typeDay
    }

    /*@Throws(ParseException::class)
    fun getRemainingClosestTime(timeList: List<String>, currentTime: String): ResultTime {
        val hiLoTimes = closestTime(timeList, currentTime)
        val loClosestTime = hiLoTimes[0]
        val hiClosestTime = hiLoTimes[1]
        var subTime: Long
        var flag = false
        var remainingTime: String? = ConstantUtils.TIME_EMPTY
        if (loClosestTime!!.isNotEmpty()) {
            subTime = (
                parseTimeToMilliseconds(loClosestTime) -
                    parseTimeToMilliseconds(currentTime)
                )
            if (Math.abs(subTime) < TimeUnit.MINUTES.toMillis(ConstantUtils.MINUTES_PASS)) {
                remainingTime = java.lang.String.valueOf(TimeUnit.MILLISECONDS.toMinutes(subTime))
                flag = true
            }
        }
        var closestTime: String? = ConstantUtils.TIME_EMPTY
        if (hiClosestTime!!.isNotEmpty()) {
            closestTime = hiClosestTime
            if (!flag) {
                subTime = subTime(currentTime, hiClosestTime).toLong()
                remainingTime = subTime.toString()
            }
        }
        return ResultTime(remainingTime, closestTime)
    }

    @Throws(ParseException::class)
    private fun parseTimeToMilliseconds(time: String?): Long {
        val format: DateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val date: Date = format.parse(time)
        return date.time
    }

    @Throws(ParseException::class)
    private fun parseDate(time: String): Date {
        val format: DateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        return format.parse(time)
    }

    @Throws(ParseException::class)
    private fun closestTime(timeList: List<String>, currentTime: String): Array<String?> {
        val times: NavigableSet<Date> = TreeSet()
        val now: Date = parseDate(currentTime)
        for (time in timeList) {
            if (time == currentTime) return arrayOf(ConstantUtils.EMPTY_STRING, currentTime)
            times.add(parseDate(time))
        }
        val hiLoClosetTime = arrayOfNulls<String>(2)
        val hiClosestTime: Date = times.higher(now)
        val lowClosestTime: Date = times.lower(now)
        hiLoClosetTime[0] = SimpleDateFormat("HH:mm", Locale.getDefault()).format(lowClosestTime)
        hiLoClosetTime[1] = SimpleDateFormat("HH:mm", Locale.getDefault()).format(hiClosestTime)
        return hiLoClosetTime
    }

    @Throws(ParseException::class)
    private fun subTime(currentTime: String, closestTime: String?): Long {
        val diff: Long
        val currentTimeMillis = parseTimeToMilliseconds(currentTime)
        val closestTimeMillis = parseTimeToMilliseconds(closestTime)
        diff = if (closestTimeMillis < currentTimeMillis) {
            TimeUnit.HOURS.toMillis(ConstantUtils.HOUR_PER_DAY) + closestTimeMillis - currentTimeMillis
        } else {
            closestTimeMillis - currentTimeMillis
        }
        return TimeUnit.MILLISECONDS.toMinutes(diff)
    }

    class ResultTime internal constructor(val remainingTime: String?, val closestTime: String?)

    companion object {

        private val mTempString = StringBuilder()

        val currentTime: String
            get() = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

        val currentDate: String
            get() = SimpleDateFormat("EEEE", Locale.getDefault()).format(Date())

        fun formatRemainingTime(remainingTime: String): String {
            val HOUR = 60
            val stringRemainingTime: String
            if (remainingTime == ConstantUtils.TIME_EMPTY) {
                return "-"
            }
            val intRemainingTime = remainingTime.toInt()
            stringRemainingTime = if (intRemainingTime < 0) {
                remainingTime + MINUTES
            } else {
                if (intRemainingTime < HOUR) {
                    remainingTime + MINUTES
                } else {
                    val hours = TimeUnit.MINUTES.toHours(intRemainingTime) as Int
                    val minutes = (intRemainingTime - TimeUnit.HOURS.toMinutes(hours) as Int).toLong()
                    mTempString.delete(0, mTempString.length)
                    mTempString.append(hours)
                        .append(HOURS)
                        .append(ConstantUtils.ONE_SPACE)
                        .append(minutes)
                        .append(MINUTES).toString()
                }
            }
            return stringRemainingTime
        }
    }*/
}
