package org.xtimms.ridebus.ui.stops

import android.os.Bundle
import org.xtimms.ridebus.data.database.RideBusDatabase
import org.xtimms.ridebus.data.database.entity.Stop
import org.xtimms.ridebus.ui.base.presenter.BasePresenter
import rx.Observable
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class StopsPresenter(
    private val cityId: Int,
    private val db: RideBusDatabase = Injekt.get()
) : BasePresenter<StopsController>() {

    /**
     * List containing stops.
     */
    private var stops: List<Stop> = emptyList()

    private var stopsSubscription: Subscription? = null

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        loadStops(cityId)
    }

    private fun loadStops(cityId: Int) {
        stopsSubscription?.unsubscribe()
        stopsSubscription = Observable.just(db.stopDao().getAllStops(cityId))
            .doOnNext { stops = it }
            .map { it.map(::StopsItem) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeLatestCache(StopsController::setStops)
    }
}
