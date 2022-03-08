package org.xtimms.ridebus.ui.routes.details

import android.os.Bundle
import org.xtimms.ridebus.data.database.RideBusDatabase
import org.xtimms.ridebus.data.database.entity.Route
import org.xtimms.ridebus.data.database.entity.Stop
import org.xtimms.ridebus.ui.base.presenter.BasePresenter
import org.xtimms.ridebus.ui.routes.details.stop.StopOnRouteItem
import rx.Observable
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class RouteDetailsPresenter(
    val route: Route,
    private val db: RideBusDatabase = Injekt.get()
) : BasePresenter<RouteDetailsController>() {

    /**
     * List containing stops.
     */
    private var stops: List<Stop> = emptyList()

    private var stopsSubscription: Subscription? = null

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        loadStops(route)
    }

    private fun loadStops(route: Route) {
        stopsSubscription?.unsubscribe()
        stopsSubscription = Observable.just(db.routesAndStopsDao().getStopsByRoute(route.routeId))
            .doOnNext { stops = it }
            .map { it.map(::StopOnRouteItem) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeLatestCache(RouteDetailsController::onNextStops)
    }
}
