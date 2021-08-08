package org.xtimms.ridebus.data.preference

import org.xtimms.ridebus.R

object PreferenceValues {

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
        DARK_BLUE(R.string.theme_darkblue),
        GREEN_APPLE(R.string.theme_greenapple),
        PINK(R.string.theme_pink),
        ORANGE(R.string.theme_orange),
        TEAL(R.string.theme_teal)
    }

}