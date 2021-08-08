package org.xtimms.ridebus.util.system

import android.app.Application
import android.content.res.Configuration
import android.os.Build
import android.view.ContextThemeWrapper
import androidx.core.os.ConfigurationCompat
import org.xtimms.ridebus.data.preference.PreferencesHelper
import uy.kohesive.injekt.injectLazy
import java.util.*

@Suppress("DEPRECATION")
object LocaleHelper {

    private val preferences: PreferencesHelper by injectLazy()

    private var systemLocale: Locale? = null

    /**
     * The application's locale. When it's null, the system locale is used.
     */
    private var appLocale = getLocaleFromString(preferences.lang())

    /**
     * The currently applied locale. Used to avoid losing the selected language after a non locale
     * configuration change to the application.
     */
    private var currentLocale: Locale? = null

    /**
     * Returns the locale for the value stored in preferences, or null if it's system language.
     *
     * @param pref the string value stored in preferences.
     */
    fun getLocaleFromString(pref: String?): Locale? {
        if (pref.isNullOrEmpty()) {
            return null
        }
        return getLocale(pref)
    }

    /**
     * Returns Display name of a string language code
     */
    fun getDisplayName(lang: String?): String {
        return when (lang) {
            null -> ""
            "" -> {
                systemLocale!!.getDisplayName(systemLocale).capitalize()
            }
            else -> {
                val locale = getLocale(lang)
                locale.getDisplayName(locale).capitalize()
            }
        }
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
     * Changes the application's locale with a new preference.
     *
     * @param pref the new value stored in preferences.
     */
    fun changeLocale(pref: String) {
        appLocale = getLocaleFromString(pref)
    }

    /**
     * Updates the app's language to an activity.
     */
    fun updateConfiguration(wrapper: ContextThemeWrapper) {
        if (appLocale != null) {
            val config = Configuration(preferences.context.resources.configuration)
            config.setLocale(appLocale)
            wrapper.applyOverrideConfiguration(config)
        }
    }

    /**
     * Updates the app's language to the application.
     */
    fun updateConfiguration(app: Application, config: Configuration, configChange: Boolean = false) {
        if (systemLocale == null) {
            systemLocale = ConfigurationCompat.getLocales(config)[0]
        }
        if (configChange) {
            val configLocale = ConfigurationCompat.getLocales(config)[0]
            if (currentLocale == configLocale) {
                return
            }
            systemLocale = configLocale
        }
        currentLocale = appLocale ?: systemLocale ?: Locale.getDefault()
        val newConfig = updateConfigLocale(config, currentLocale!!)
        val resources = app.resources
        resources.updateConfiguration(newConfig, resources.displayMetrics)

        Locale.setDefault(currentLocale)
    }

    /**
     * Returns a new configuration with the given locale applied.
     */
    private fun updateConfigLocale(config: Configuration, locale: Locale): Configuration {
        val newConfig = Configuration(config)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            newConfig.locale = locale
        } else {
            newConfig.setLocale(locale)
        }
        return newConfig
    }
}
