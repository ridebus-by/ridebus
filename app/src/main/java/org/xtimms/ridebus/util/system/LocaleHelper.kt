package org.xtimms.ridebus.util.system

import android.content.Context
import androidx.core.os.LocaleListCompat
import org.xtimms.ridebus.R
import org.xtimms.ridebus.ui.favourite.FavouritesPresenter
import java.util.*

/**
 * Utility class to change the application's language in runtime.
 */
object LocaleHelper {

    /**
     * Returns Display name of a favourite type
     */
    fun getTypeDisplayName(type: String?, context: Context): String {
        return when (type) {
            FavouritesPresenter.PINNED_KEY -> context.getString(R.string.pinned)
            "other" -> context.getString(R.string.other)
            else -> getDisplayName(type)
        }
    }

    /**
     * Returns Display name of a string language code
     *
     * @param lang empty for system language
     */
    private fun getDisplayName(lang: String?): String {
        if (lang == null) {
            return ""
        }

        val locale = if (lang.isEmpty()) {
            LocaleListCompat.getAdjustedDefault()[0]
        } else {
            getLocale(lang)
        }
        return locale!!.getDisplayName(locale).replaceFirstChar { it.uppercase(locale) }
    }

    /**
     * Return Locale from string language code
     */
    private fun getLocale(lang: String): Locale {
        val sp = lang.split("_", "-")
        return when (sp.size) {
            2 -> Locale(sp[0], sp[1])
            3 -> Locale(sp[0], sp[1], sp[2])
            else -> Locale(lang)
        }
    }
}
