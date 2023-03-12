package org.xtimms.ridebus.util

enum class TypeOfDay(val idInDatabase: Int) {
    WEEKDAYS(1),
    WEEKEND(2),
    ON_FRIDAY(5),
    ON_MONDAY_THURSDAY(6),
    EVERYDAY(7);
}
