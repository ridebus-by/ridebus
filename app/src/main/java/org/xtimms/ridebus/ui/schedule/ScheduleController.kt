package org.xtimms.ridebus.ui.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import dev.chrisbanes.insetter.applyInsetter
import eu.davidea.flexibleadapter.FlexibleAdapter
import org.xtimms.ridebus.R
import org.xtimms.ridebus.data.database.RideBusDatabase
import org.xtimms.ridebus.data.database.entity.Route
import org.xtimms.ridebus.data.database.entity.Stop
import org.xtimms.ridebus.databinding.ScheduleControllerBinding
import org.xtimms.ridebus.ui.base.controller.NoAppBarElevationController
import org.xtimms.ridebus.ui.base.controller.NucleusController
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class ScheduleController : // TODO
    NucleusController<ScheduleControllerBinding, SchedulePresenter>,
    NoAppBarElevationController,
    FlexibleAdapter.OnUpdateListener {

    constructor(route: Route?, stop: Stop?) : super(
        bundleOf(
            ROUTE_EXTRA to (route?.routeId ?: 0),
            STOP_EXTRA to (stop?.stopId ?: 0)
        )
    ) {
        this.route = route
        this.stop = stop
    }

    constructor(routeId: Int, stopId: Int) : this(
        Injekt.get<RideBusDatabase>().routeDao().getRoute(routeId).firstOrNull(),
        Injekt.get<RideBusDatabase>().stopDao().getStop(stopId).firstOrNull()
    )

    @Suppress("unused")
    constructor(bundle: Bundle) : this(
        bundle.getInt(ROUTE_EXTRA),
        bundle.getInt(STOP_EXTRA)
    )

    var route: Route? = null
        private set

    var stop: Stop? = null
        private set

    private var adapter: ScheduleAdapter? = null

    override fun getTitle(): String? {
        return resources?.getString(R.string.schedule)
    }

    override fun createBinding(inflater: LayoutInflater) =
        ScheduleControllerBinding.inflate(inflater)

    override fun createPresenter(): SchedulePresenter {
        return SchedulePresenter()
    }

    override fun onViewCreated(view: View) {
        super.onViewCreated(view)

        binding.recycler.applyInsetter {
            type(navigationBars = true) {
                padding()
            }
        }

        adapter = ScheduleAdapter(this)
        binding.recycler.adapter = adapter
        binding.recycler.layoutManager = LinearLayoutManager(view.context)
        binding.recycler.setHasFixedSize(true)

        binding.number.text = route?.number
        binding.routeTitle.text = route?.title

        binding.stopTitle.text = stop?.name
        binding.stopDescription.text = stop?.direction
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

    companion object {
        const val ROUTE_EXTRA = "route"
        const val STOP_EXTRA = "stop"
    }
}
