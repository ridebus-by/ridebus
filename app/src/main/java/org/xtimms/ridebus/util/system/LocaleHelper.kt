package org.xtimms.ridebus.util.system

import android.content.Context
import org.xtimms.ridebus.R
import org.xtimms.ridebus.ui.favourite.FavouritesPresenter

/**
 * Utility class to change the application's language in runtime.
 */
object LocaleHelper {

    private const val BUS_ID = 1
    private const val ROUTE_TAXI_ID = 2
    private const val EXPRESS_ID = 3
    private const val TRAM_ID = 4

    /**
     * Returns Display name of a favourite type
     */
    fun getTypeDisplayName(type: Int?, context: Context): String {
        return when (type) {
            FavouritesPresenter.LAST_USED_KEY -> context.getString(R.string.last_used)
            FavouritesPresenter.PINNED_KEY -> context.getString(R.string.pinned)
            BUS_ID -> context.getString(R.string.label_bus)
            ROUTE_TAXI_ID -> context.getString(R.string.label_route_taxi)
            EXPRESS_ID -> context.getString(R.string.label_express)
            TRAM_ID -> context.getString(R.string.label_tram)
            else -> context.getString(R.string.other)
        }
    }
}
