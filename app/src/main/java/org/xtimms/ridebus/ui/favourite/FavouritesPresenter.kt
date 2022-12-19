package org.xtimms.ridebus.ui.favourite

import android.os.Bundle
import org.xtimms.ridebus.data.database.RideBusDatabase
import org.xtimms.ridebus.data.database.entity.Route
import org.xtimms.ridebus.data.preference.PreferencesHelper
import org.xtimms.ridebus.ui.base.presenter.BasePresenter
import rx.Observable
import rx.Subscription
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get
import uy.kohesive.injekt.injectLazy
import java.util.TreeMap

class FavouritesPresenter(
    private val preferences: PreferencesHelper = Injekt.get()
) : BasePresenter<FavouritesController>() {

    private val db: RideBusDatabase by injectLazy()

    private var favouriteSubscription: Subscription? = null

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        loadFavourites()
    }

    private fun loadFavourites() {
        favouriteSubscription?.unsubscribe()
        val favourites = mutableListOf<Route?>()
        val favouriteIds = preferences.favourites().get()
        val pinnedFavourites = mutableListOf<FavouriteItem>()
        val pinnedFavouriteIds = preferences.pinnedFavourites().get()

        for (element in favouriteIds) {
            favourites += db.routeDao().getRoute(element.toInt())
        }

        val map = TreeMap<Int, MutableList<Route?>> { d1, d2 ->
            // Routes without a transport type defined will be placed at the end
            when {
                d1 == 0 && d2 != 0 -> 1
                d2 == 0 && d1 != 0 -> -1
                else -> d1.compareTo(d2)
            }
        }

        val byType = favourites.sortedWith(object : Comparator<Route?> {

            override fun compare(o1: Route?, o2: Route?): Int {
                return extractInt(o1!!) - extractInt(o2!!)
            }

            fun extractInt(s: Route): Int {
                val num = s.number.replace("\\D".toRegex(), "")
                return if (num.isEmpty()) 0 else Integer.parseInt(num)
            }
        }).groupByTo(map) { it?.transportId ?: EMPTY }
        var favouriteItems = byType.flatMap {
            val typeItem = TypeItem(it.key)
            it.value.map { favourite ->
                val isPinned = favourite?.routeId.toString() in pinnedFavouriteIds
                if (isPinned) {
                    pinnedFavourites.add(FavouriteItem(favourite, TypeItem(PINNED_KEY), isPinned))
                }
                FavouriteItem(favourite, typeItem, isPinned)
            }
        }

        if (pinnedFavourites.isNotEmpty()) {
            favouriteItems = pinnedFavourites + favouriteItems
        }

        favouriteSubscription = Observable.just(favouriteItems)
            .subscribeLatestCache(FavouritesController::setFavourites)
    }

    fun updateFavourites() {
        loadFavourites()
    }

    companion object {
        const val EMPTY = 0
        const val PINNED_KEY = 100
        const val LAST_USED_KEY = 101
    }
}
