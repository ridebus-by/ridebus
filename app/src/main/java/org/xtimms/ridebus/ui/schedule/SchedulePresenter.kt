package org.xtimms.ridebus.ui.schedule

import android.os.Bundle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import org.xtimms.ridebus.data.database.RideBusDatabase
import org.xtimms.ridebus.data.database.entity.Route
import org.xtimms.ridebus.data.database.entity.Stop
import org.xtimms.ridebus.ui.base.presenter.BasePresenter
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get
import java.util.NavigableSet
import java.util.TreeSet
import java.util.concurrent.TimeUnit

class SchedulePresenter(
    private val typeDay: Int,
    private val route: Route,
    private val stop: Stop,
    private val db: RideBusDatabase = Injekt.get()
) : BasePresenter<ScheduleController>() {

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        observeSchedule(typeDay, route, stop)
    }

    private fun observeSchedule(typeDay: Int, route: Route, stop: Stop) {
        combine(
            db.scheduleDao().observeArrivalTime(typeDay, route.routeId, stop.stopId),
            tickerFlow()
        ) { schedule, time ->
            val rows = HashMap<Int, NavigableSet<Int>>(schedule.size)
            for (item in schedule) {
                val hour = item.hour ?: continue
                val minute = item.minute ?: continue
                rows.getOrPut(hour) { TreeSet() }.add(minute)
            }
            rows.entries.map { (hour, minutesInHour) ->
                ScheduleRow(hour, minutesInHour, time)
            }
        }.flowOn(Dispatchers.Default)
            .onEach { view?.setSchedule(it) }
            .launchIn(presenterScope)
    }

    private fun tickerFlow() = flow {
        while (isActive) {
            emit(System.currentTimeMillis())
            delay(TimeUnit.MINUTES.toMillis(1))
        }
    }
}
