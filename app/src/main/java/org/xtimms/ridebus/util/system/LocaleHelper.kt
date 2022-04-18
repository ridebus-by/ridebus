package org.xtimms.ridebus.util.system

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import androidx.core.os.LocaleListCompat
import org.xtimms.ridebus.R
import org.xtimms.ridebus.data.preference.PreferencesHelper
import org.xtimms.ridebus.ui.favourite.FavouritesPresenter
import uy.kohesive.injekt.injectLazy
import java.util.*

/**
 * Utility class to change the application's language in runtime.
 */
object LocaleHelper {

    private val preferences: PreferencesHelper by injectLazy()

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
    fun getDisplayName(lang: String?): String {
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
     * Creates a ContextWrapper using selected Locale
     */
    fun createLocaleWrapper(context: Context): ContextWrapper {
        val appLocale = getLocaleFromString(preferences.lang().get())
        val newConfiguration = Configuration(context.resources.configuration)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val localeList = LocaleList(appLocale)
            newConfiguration.setLocales(localeList)
        } else {
            newConfiguration.setLocale(appLocale)
        }
        return ContextWrapper(context.createConfigurationContext(newConfiguration))
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

    /**
     * Returns the locale for the value stored in preferences, defaults to main system language.
     *
     * @param pref the string value stored in preferences.
     */
    private fun getLocaleFromString(pref: String?): Locale {
        if (pref.isNullOrEmpty()) {
            return LocaleListCompat.getDefault()[0]!!
        }
        return getLocale(pref)
    }
}
