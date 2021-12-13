package org.xtimms.ridebus.util.lang

import java.util.*
import kotlin.math.floor

fun String.truncateCenter(count: Int, replacement: String = "..."): String {
    if (length <= count) {
        return this
    }

    val pieceLength: Int = floor((count - replacement.length).div(2.0)).toInt()

    return "${take(pieceLength)}$replacement${takeLast(pieceLength)}"
}

fun String.transliterate(skipMissing: Boolean): String {
    val cyr = charArrayOf(
        'а', 'б', 'в', 'г', 'д', 'е', 'ж', 'з', 'и', 'й', 'к', 'л', 'м', 'н', 'о', 'п',
        'р', 'с', 'т', 'у', 'ф', 'х', 'ц', 'ч', 'ш', 'щ', 'ъ', 'ы', 'ь', 'э', 'ю', 'я', 'ё'
    )
    val lat = arrayOf(
        "a", "b", "v", "g", "d", "e", "zh", "z", "i", "y", "k", "l", "m", "n", "o", "p",
        "r", "s", "t", "u", "f", "h", "ts", "ch", "sh", "sch", "", "i", "", "e", "ju", "ja", "jo"
    )
    return buildString(length + 5) {
        for (c in this@transliterate) {
            val p = cyr.binarySearch(c.toLowerCase())
            if (p in lat.indices) {
                if (c.isUpperCase()) {
                    append(lat[p].toUpperCase(Locale.getDefault()))
                } else {
                    append(lat[p])
                }
            } else if (!skipMissing) {
                append(c)
            }
        }
    }
}
