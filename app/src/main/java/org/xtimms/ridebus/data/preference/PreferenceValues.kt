package org.xtimms.ridebus.data.preference

import org.xtimms.ridebus.R

object PreferenceValues {

    /* ktlint-disable experimental:enum-entry-name-case */

    enum class City {
        POLOTSK,
        NOVOPOLOTSK
    }

    /* ktlint-disable experimental:enum-entry-name-case */

    // Keys are lowercase to match legacy string values
    enum class ThemeMode {
        light,
        dark,
        system,
    }

    /* ktlint-enable experimental:enum-entry-name-case */

    enum class AppTheme(val titleResId: Int?) {
        DEFAULT(R.string.theme_default),
        MONET(R.string.theme_monet),
        POMEGRANATE(R.string.theme_pomegranate),
        HONEY(R.string.theme_honey),
        GREEN_APPLE(R.string.theme_greenapple),
        GALAXY(R.string.theme_galaxy),
        PINK(R.string.theme_balloon),
    }

    enum class TabletUiMode {
        ALWAYS,
        LANDSCAPE,
        NEVER
    }
}
