package org.xtimms.ridebus.util.system

import android.content.Context
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
    fun getTypeDisplayName(type: Int?, context: Context): String {
        return when (type) {
            FavouritesPresenter.PINNED_KEY -> context.getString(R.string.pinned)
            else -> context.getString(R.string.other)
        }
    }
}
