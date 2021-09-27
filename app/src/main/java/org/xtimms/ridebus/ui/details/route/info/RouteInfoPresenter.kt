package org.xtimms.ridebus.ui.details.route.info

import android.os.Bundle
import org.xtimms.ridebus.data.database.entity.Route
import org.xtimms.ridebus.ui.base.presenter.BasePresenter
import rx.Observable
import rx.Subscription

class RouteInfoPresenter(val route: Route) : BasePresenter<RouteInfoController>() {

    /**
     * Subscription to send the route to the view.
     */
    private var viewRouteSubscription: Subscription? = null

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        sendRouteToView()
    }

    private fun sendRouteToView() {
        viewRouteSubscription?.let { remove(it) }
        viewRouteSubscription = Observable.just(route)
            .subscribeLatestCache({ view, route -> view.onNextRoute(route) })
    }
}
