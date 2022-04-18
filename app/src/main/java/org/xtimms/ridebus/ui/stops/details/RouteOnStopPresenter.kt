package org.xtimms.ridebus.ui.stops.details

import android.os.Bundle
import org.xtimms.ridebus.data.database.RideBusDatabase
import org.xtimms.ridebus.data.database.entity.Route
import org.xtimms.ridebus.data.database.entity.Stop
import org.xtimms.ridebus.ui.base.presenter.BasePresenter
import rx.Observable
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class RouteOnStopPresenter(
    val stop: Stop,
    private val db: RideBusDatabase = Injekt.get()
) : BasePresenter<RoutesOnStopController>() {

    /**
     * List containing stops.
     */
    private var stops: List<Route> = emptyList()

    private var stopsSubscription: Subscription? = null

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        loadRoutesOnStop(stop.stopId)
    }

    private fun loadRoutesOnStop(stopId: Int) {
        stopsSubscription?.unsubscribe()
        stopsSubscription = Observable.just(db.routesAndStopsDao().getRoutesByStop(stopId))
            .doOnNext { stops = it }
            .map { it.map(::RoutesOnStopItem) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeLatestCache(RoutesOnStopController::onNextStop)
    }
}
