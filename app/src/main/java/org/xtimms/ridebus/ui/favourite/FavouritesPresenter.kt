package org.xtimms.ridebus.ui.favourite

import android.os.Bundle
import org.xtimms.ridebus.data.database.entity.Route
import org.xtimms.ridebus.data.preference.PreferencesHelper
import org.xtimms.ridebus.ui.base.presenter.BasePresenter
import rx.Observable
import rx.Subscription
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class FavouritesPresenter(
    private val preferences: PreferencesHelper = Injekt.get()
) : BasePresenter<FavouritesController>() {

    var favourites = mutableListOf( // TODO
        Route(
            1,
            0,
            1,
            1,
            "1",
            "Швейная фабрика − Богатырская",
            "через мкр-н Громы в д. Богатырская",
            "0,80 BYN",
            "ежедневно",
            "05:25 - 20:12",
            "Октябрьская улица - улица Гагарина - улица Суворова - д.Богатырская",
            "ОАО \"Витебскоблавтотранс\" филиал \"Автобусный парк №2 г. Полоцка\"",
            "Неизвестно",
            1,
            0,
            0,
            1,
            1
        ),
    )

    private var favouriteSubscription: Subscription? = null

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        loadFavourites()
    }

    private fun loadFavourites() {
        favouriteSubscription?.unsubscribe()

        val pinnedFavourites = mutableListOf<FavouriteItem>()
        val pinnedFavouriteIds = preferences.pinnedFavourites().get()

        val byType = favourites.groupBy { it.kindId }
        var favouriteItems = byType.flatMap {
            val typeItem = TypeItem(it.key) // TODO
            it.value.map { favourite ->
                val isPinned = favourite.routeId.toString() in pinnedFavouriteIds
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
        const val PINNED_KEY = 0b00000000
    }
}
