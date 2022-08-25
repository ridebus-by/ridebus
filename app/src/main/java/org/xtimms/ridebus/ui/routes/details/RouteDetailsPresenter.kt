package org.xtimms.ridebus.ui.routes.details

import android.os.Bundle
import org.xtimms.ridebus.data.database.RideBusDatabase
import org.xtimms.ridebus.data.database.entity.Route
import org.xtimms.ridebus.data.database.entity.Stop
import org.xtimms.ridebus.ui.base.presenter.BasePresenter
import org.xtimms.ridebus.ui.routes.details.stop.StopOnRouteItem
import org.xtimms.ridebus.util.Times
import org.xtimms.ridebus.util.system.TimeUtil
import rx.Observable
import rx.Single
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get
import java.text.SimpleDateFormat
import java.util.*

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
        stopsSubscription = Single.fromCallable {
            db.routesAndStopsDao().getStopsByRoute(route.routeId)
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess { stops = it }
            .observeOn(Schedulers.computation())
            .flatMapObservable { Observable.from(it) }
            .flatMapSingle { stop ->
                getTimes(
                    TimeUtil.getTypeDay(
                        SimpleDateFormat("EEEE", Locale.getDefault()).format(Date())
                    ),
                    stop.stopId
                ).map { times -> StopOnRouteItem(stop, times) }
            }.toSortedList { p1, p2 ->
                p1?.stop?.stopId?.compareTo(p2?.stop?.stopId!!) // FIXME Sort by number of stop on route
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeLatestCache(RouteDetailsController::onNextStops)
    }

    private fun getTimes(typeDay: Int, stopId: Int): Single<Times> {
        return Single.fromCallable {
            db.scheduleDao().getArrivalTimeOnStop(typeDay, route.routeId, stopId)
                .map { it.time }
        }.subscribeOn(Schedulers.io())
            .map { Times(it) }
    }
}
