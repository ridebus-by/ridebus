package org.xtimms.ridebus.ui.routes

import android.os.Bundle
import org.xtimms.ridebus.data.database.RideBusDatabase
import org.xtimms.ridebus.data.database.entity.Route
import org.xtimms.ridebus.data.preference.PreferencesHelper
import org.xtimms.ridebus.ui.base.presenter.BasePresenter
import rx.Observable
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class RoutePresenter(
    private val transportType: Int,
    private val db: RideBusDatabase = Injekt.get(),
    private val preferences: PreferencesHelper = Injekt.get()
) : BasePresenter<RouteController>() {

    private var routes: List<Route> = emptyList()

    private var routesSubscription: Subscription? = null

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        getRoutes(transportType)
    }

    private fun getRoutes(transportType: Int) {
        routesSubscription?.unsubscribe()
        routesSubscription = Observable.just(db.routeDao().getRoutes(transportType, preferences.city().get().toInt()))
            .doOnNext { routes = it }
            .map { it.map(::RouteItem) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeLatestCache(RouteController::setRoutes)
    }
}
