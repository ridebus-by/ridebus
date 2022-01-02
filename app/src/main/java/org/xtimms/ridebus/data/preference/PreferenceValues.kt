package org.xtimms.ridebus.data.preference

import org.xtimms.ridebus.R

object PreferenceValues {

    /* ktlint-disable experimental:enum-entry-name-case */

    enum class City {
        POLOTSK,
        NOVOPOLOTSK,
        USHACHI
    }

    /* ktlint-disable experimental:enum-entry-name-case */

    // Keys are lowercase to match legacy string values
    enum class ThemeMode {
        light,
        dark,
        system,
    }

    /* ktlint-enable experimental:enum-entry-name-case */

    enum class AppTheme(val titleResId: Int) {
        DEFAULT(R.string.theme_default),
        MONET(R.string.theme_monet),
        POMEGRANATE(R.string.theme_pomegranate),
        ORANGE(R.string.theme_orange),
        SUNFLOWER(R.string.theme_sunflower),
        GREEN_APPLE(R.string.theme_greenapple),
        PROTOSS_PYLON(R.string.theme_protosspylon),
        BLUEBELL(R.string.theme_bluebell),
        PINK(R.string.theme_pink),
    }

    enum class TabletUiMode {
        ALWAYS,
        LANDSCAPE,
        NEVER
    }
}
