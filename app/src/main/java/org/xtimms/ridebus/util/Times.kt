package org.xtimms.ridebus.util

import java.util.*

class Times(times: Collection<String>) {

    private val data = TreeSet(times.map { Time(it) })

    fun closest(time: Time): Time? {
        return data.ceiling(time)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Times

        if (data != other.data) return false

        return true
    }

    override fun hashCode(): Int {
        return data.hashCode()
    }

    class Time(
        val hours: Int,
        val minutes: Int
    ) : Comparable<Time> {

        constructor(str: String) : this(
            str.substringBefore(':').toInt(),
            str.substringAfter(':').toInt()
        )

        override fun compareTo(other: Time): Int {
            var diff = compareValues(hours, other.hours)
            if (diff == 0) {
                diff = compareValues(minutes, other.minutes)
            }
            return diff
        }

        operator fun minus(other: Time): Time {
            var h = hours - other.hours
            var m = minutes - other.minutes
            if (m < 0) {
                h--
                m += 60
            }
            return Time(h, m)
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Time

            if (hours != other.hours) return false
            if (minutes != other.minutes) return false

            return true
        }

        override fun hashCode(): Int {
            var result = hours
            result = 31 * result + minutes
            return result
        }

        override fun toString(): String {
            return "%d:%02d".format(hours, minutes)
        }

        companion object {

            fun now(): Time {
                val cal = Calendar.getInstance()
                return Time(cal[Calendar.HOUR_OF_DAY], cal[Calendar.MINUTE])
            }
        }
    }
}
