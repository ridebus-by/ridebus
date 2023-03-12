package org.xtimms.ridebus.ui.schedule

import android.os.Bundle
import org.xtimms.ridebus.data.database.RideBusDatabase
import org.xtimms.ridebus.data.database.dao.ScheduleDao.Timetable
import org.xtimms.ridebus.data.database.entity.Route
import org.xtimms.ridebus.data.database.entity.Stop
import org.xtimms.ridebus.ui.base.presenter.BasePresenter
import rx.Observable
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class SchedulePresenter(
    private val typeDay: Int,
    private val route: Route,
    private val stop: Stop,
    private val db: RideBusDatabase = Injekt.get()
) : BasePresenter<ScheduleController>() {

    private var timeList: List<Timetable> = emptyList()

    private var scheduleSubscription: Subscription? = null

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        loadSchedule(typeDay, route, stop)
    }

    private fun loadSchedule(typeDay: Int, route: Route, stop: Stop) {
        scheduleSubscription?.unsubscribe()
        scheduleSubscription = Observable.just(db.scheduleDao().getArrivalTime(typeDay, route.routeId, stop.stopId))
            .doOnNext { timeList = it }
            .map { it.map(::ScheduleItem) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeLatestCache(ScheduleController::setSchedule)
    }
}
