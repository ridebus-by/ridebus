package org.xtimms.ridebus.ui.routes.details

import android.os.Bundle
import logcat.LogPriority
import org.xtimms.ridebus.data.database.RideBusDatabase
import org.xtimms.ridebus.data.database.entity.Route
import org.xtimms.ridebus.data.database.entity.Stop
import org.xtimms.ridebus.ui.base.presenter.BasePresenter
import org.xtimms.ridebus.ui.routes.details.stop.StopOnRouteItem
import org.xtimms.ridebus.util.Times
import org.xtimms.ridebus.util.system.logcat
import rx.Observable
import rx.Single
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class RouteDetailsPresenter(
    val route: Route,
    private val db: RideBusDatabase = Injekt.get()
) : BasePresenter<RouteDetailsController>() {

    /**
     * List containing stops.
     */
    private var stops: List<IndexedValue<Stop>> = emptyList()

    private var stopsSubscription: Subscription? = null

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        loadStops(route)
    }

    private fun loadStops(route: Route) {
        stopsSubscription?.unsubscribe()
        stopsSubscription = Single.fromCallable {
            db.routesAndStopsDao().getStopsByRoute(route.routeId).withIndex()
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess { stops = it.toList() }
            .observeOn(Schedulers.computation())
            .flatMapObservable { Observable.from(it) }
            .flatMapSingle { (index, stop) ->
                getTimes(
                    db.scheduleDao().getTypesOfDay(route.routeId)[0],
                    stop.stopId
                ).map { times -> StopOnRouteItem(stop, times, index) }
            }.toSortedList()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeLatestCache(
                { view, it ->
                    view.onNextStops(it)
                },
                { _, error ->
                    logcat(LogPriority.ERROR, error)
                    view?.onError()
                }
            )
    }

    private fun getTimes(typeDay: Int?, stopId: Int): Single<Times> {
        return Single.fromCallable {
            db.scheduleDao().getArrivalTime(checkNotNull(typeDay), route.routeId, stopId)
                .map { it.arrivalTime }
        }.subscribeOn(Schedulers.io())
            .map { Times(it) }
    }
}
