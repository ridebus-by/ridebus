package org.xtimms.ridebus.ui.details.stops

import android.os.Bundle
import org.xtimms.ridebus.data.database.entity.Route
import org.xtimms.ridebus.ui.base.presenter.BasePresenter
import rx.Observable
import rx.Subscription

class RouteStopsPresenter(val route: Route) : BasePresenter<RouteStopsController>() {

    /**
     * Subscription to send stops list to the view.
     */
    private var viewStopsOnRouteSubscription: Subscription? = null

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        sendRouteToView()
    }

    private fun sendRouteToView() {
        viewStopsOnRouteSubscription?.let { remove(it) }
        viewStopsOnRouteSubscription = Observable.just(route)
            .subscribeLatestCache({ view, route -> view.onNextRoute(route) })
    }
}
