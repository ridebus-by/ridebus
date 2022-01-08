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

fun String.transliterate(): String {
    val cyr = charArrayOf(
        'а', 'б', 'в', 'г', 'д', 'е', 'ж', 'з', 'и', 'й', 'к', 'л', 'м', 'н', 'о', 'п',
        'р', 'с', 'т', 'у', 'ф', 'х', 'ц', 'ч', 'ш', 'щ', 'ъ', 'ы', 'ь', 'э', 'ю', 'я', 'ё',
        'А', 'Б', 'В', 'Г', 'Д', 'Е', 'Ж', 'З', 'И', 'Й', 'К', 'Л', 'М', 'Н', 'О', 'П',
        'Р', 'С', 'Т', 'У', 'Ф', 'Х', 'Ц', 'Ч', 'Ш', 'Щ', 'Ъ', 'Ы', 'Ь', 'Э', 'Ю', 'Я', 'Ё',
        ' ', '-', '−', '/', '(', ')', '«', '»', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
    )
    val lat = arrayOf(
        "a", "b", "v", "g", "d", "e", "zh", "z", "i", "y", "k", "l", "m", "n", "o", "p",
        "r", "s", "t", "u", "f", "kh", "ts", "ch", "sh", "sch", "", "i", "'", "e", "yu", "ya", "yo",
        "A", "B", "V", "G", "D", "E", "Zh", "Z", "I", "Y", "K", "L", "M", "N", "O", "P",
        "R", "S", "T", "U", "F", "Kh", "Ts", "Ch", "Sh", "Sch", "", "I", "'", "E", "Yu", "Ya", "Yo",
        " ", "-", "−", "/", "(", ")", "«", "»", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"
    )
    return buildString(length + 5) {
        for (element in this@transliterate) {
            for (x in cyr.indices) {
                if (element == cyr[x]) {
                    append(lat[x])
                }
            }
        }
    }
}
