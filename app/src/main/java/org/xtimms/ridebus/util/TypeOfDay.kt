package org.xtimms.ridebus.util

enum class TypeOfDay(val idInDatabase: Int) {
    WEEKDAYS(1),
    WEEKEND(2),
    ON_SATURDAYS(3),
    ON_SUNDAYS(4),
    ON_FRIDAY(5);
}
