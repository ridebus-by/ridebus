package org.xtimms.ridebus.ui.schedule

import android.os.Bundle
import kotlinx.coroutines.launch
import org.xtimms.ridebus.data.database.RideBusDatabase
import org.xtimms.ridebus.data.database.entity.Route
import org.xtimms.ridebus.data.database.entity.Stop
import org.xtimms.ridebus.ui.base.presenter.BasePresenter
import org.xtimms.ridebus.util.lang.withUIContext
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class SchedulePresenter(
    private val typeDay: Int,
    private val route: Route,
    private val stop: Stop,
    private val db: RideBusDatabase = Injekt.get()
) : BasePresenter<ScheduleController>() {

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        loadSchedule(typeDay, route, stop)
    }

    private fun loadSchedule(typeDay: Int, route: Route, stop: Stop) {
        presenterScope.launch {
            val arrivalTimes = db.scheduleDao().getArrivalTime(typeDay, route.routeId, stop.stopId)
            withUIContext {
                view?.setSchedule(arrivalTimes.map(::ScheduleItem))
            }
        }
    }
}
