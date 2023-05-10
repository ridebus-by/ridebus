package org.xtimms.ridebus.ui.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import dev.chrisbanes.insetter.applyInsetter
import eu.davidea.flexibleadapter.FlexibleAdapter
import org.xtimms.ridebus.R
import org.xtimms.ridebus.data.database.RideBusDatabase
import org.xtimms.ridebus.data.database.entity.Route
import org.xtimms.ridebus.data.database.entity.Stop
import org.xtimms.ridebus.databinding.ScheduleControllerBinding
import org.xtimms.ridebus.ui.base.controller.NucleusController
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get
import uy.kohesive.injekt.injectLazy

class ScheduleController :
    NucleusController<ScheduleControllerBinding, SchedulePresenter>,
    FlexibleAdapter.OnUpdateListener {

    constructor(typeDay: Int, route: Route?, stop: Stop?) : super(
        Bundle().apply {
            putInt(TYPEDAY_EXTRA, typeDay)
            putInt(ROUTE_EXTRA, route?.routeId ?: 0)
            putInt(STOP_EXTRA, stop?.stopId ?: 0)
        }
    ) {
        this.typeDay = typeDay
        this.route = route
        this.stop = stop
    }

    constructor(typeDay: Int, routeId: Int, stopId: Int) : this(
        typeDay,
        Injekt.get<RideBusDatabase>().routeDao().getRoute(routeId).firstOrNull(),
        Injekt.get<RideBusDatabase>().stopDao().getStop(stopId).firstOrNull()
    )

    @Suppress("unused")
    constructor(bundle: Bundle) : this(
        bundle.getInt(TYPEDAY_EXTRA),
        bundle.getInt(ROUTE_EXTRA),
        bundle.getInt(STOP_EXTRA)
    )

    var typeDay: Int? = null
        private set

    var route: Route? = null
        private set

    var stop: Stop? = null
        private set

    private val db: RideBusDatabase by injectLazy()

    private var adapter: ScheduleAdapter? = null

    private var schedule: List<ScheduleRow> = emptyList()

    override fun createBinding(inflater: LayoutInflater) = ScheduleControllerBinding.inflate(inflater)

    override fun createPresenter(): SchedulePresenter {
        return SchedulePresenter(typeDay!!, route!!, stop!!, db)
    }

    override fun onViewCreated(view: View) {
        super.onViewCreated(view)

        binding.recycler.applyInsetter {
            type(navigationBars = true) {
                padding()
            }
        }

        adapter = ScheduleAdapter(this)
        binding.recycler.layoutManager = LinearLayoutManager(view.context)
        binding.recycler.adapter = adapter
        binding.recycler.setHasFixedSize(true)
    }

    override fun onDestroyView(view: View) {
        adapter = null
        super.onDestroyView(view)
    }

    override fun onUpdateEmptyView(size: Int) {
        if (size > 0) {
            binding.emptyView.hide()
        } else {
            binding.emptyView.show(R.drawable.ic_alert, R.string.information_no_schedule_on_route)
        }
    }

    fun setSchedule(schedule: List<ScheduleRow>) {
        this.schedule = schedule
        adapter?.updateDataSet(schedule)
    }

    companion object {
        const val TYPEDAY_EXTRA = "typeDay"
        const val ROUTE_EXTRA = "route"
        const val STOP_EXTRA = "stop"
    }
}
