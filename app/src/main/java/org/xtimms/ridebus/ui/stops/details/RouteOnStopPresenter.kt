package org.xtimms.ridebus.ui.stops.details

import android.os.Bundle
import logcat.LogPriority
import org.xtimms.ridebus.data.database.RideBusDatabase
import org.xtimms.ridebus.data.database.entity.Route
import org.xtimms.ridebus.data.database.entity.Stop
import org.xtimms.ridebus.ui.base.presenter.BasePresenter
import org.xtimms.ridebus.util.Times
import org.xtimms.ridebus.util.system.TimeUtil
import org.xtimms.ridebus.util.system.logcat
import rx.Observable
import rx.Single
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get
import java.text.SimpleDateFormat
import java.util.*

class RouteOnStopPresenter(
    val stop: Stop,
    private val db: RideBusDatabase = Injekt.get()
) : BasePresenter<RoutesOnStopController>() {

    /**
     * List containing routes.
     */
    private var routes: List<Route> = emptyList()

    private var routesSubscription: Subscription? = null

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        loadRoutesOnStop(stop)
    }

    private fun loadRoutesOnStop(stop: Stop) {
        routesSubscription?.unsubscribe()
        routesSubscription = Single.fromCallable {
            db.routesAndStopsDao().getRoutesByStop(stop.stopId)
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess { routes = it }
            .observeOn(Schedulers.computation())
            .flatMapObservable { Observable.from(it) }
            .flatMapSingle { route ->
                getTimes(
                    TimeUtil.getTypeDay(
                        SimpleDateFormat("EEEE", Locale.getDefault()).format(Date())
                    ),
                    route.routeId
                )
                    .map { times -> RoutesOnStopItem(route, times) }
            }.toSortedList { t1, t2 ->
                t1.times.closest(Times.Time.now()).toString()
                    .compareTo(t2.times.closest(Times.Time.now()).toString())
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeLatestCache(RoutesOnStopController::onNextRoute) { view, error ->
                logcat(LogPriority.ERROR, error)
                view.onNextRouteError(error)
            }
    }

    private fun getTimes(typeDay: Int, routeId: Int): Single<Times> {
        return Single.fromCallable {
            db.scheduleDao().getArrivalTime(typeDay, routeId, stop.stopId)
                .map { it.hour.toString() + ":" + it.minute.toString() }
        }.subscribeOn(Schedulers.io())
            .map { Times(it) }
    }
}
