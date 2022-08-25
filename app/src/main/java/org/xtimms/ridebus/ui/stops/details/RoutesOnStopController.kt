package org.xtimms.ridebus.ui.stops.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import dev.chrisbanes.insetter.applyInsetter
import eu.davidea.flexibleadapter.FlexibleAdapter
import org.xtimms.ridebus.R
import org.xtimms.ridebus.data.database.RideBusDatabase
import org.xtimms.ridebus.data.database.entity.Stop
import org.xtimms.ridebus.databinding.StopsRouteControllerBinding
import org.xtimms.ridebus.ui.base.controller.NoAppBarElevationController
import org.xtimms.ridebus.ui.base.controller.NucleusController
import org.xtimms.ridebus.ui.base.controller.withFadeTransaction
import org.xtimms.ridebus.ui.schedule.ScheduleTabbedController
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get
import java.text.SimpleDateFormat
import java.util.*

class RoutesOnStopController :
    NucleusController<StopsRouteControllerBinding, RouteOnStopPresenter>,
    NoAppBarElevationController,
    FlexibleAdapter.OnItemClickListener,
    FlexibleAdapter.OnUpdateListener,
    RoutesOnStopAdapter.OnItemClickListener {

    constructor(stop: Stop?) : super(
        Bundle().apply {
            putInt(STOP_EXTRA, stop?.stopId ?: 0)
        }
    ) {
        this.stop = stop
    }

    constructor(stopId: Int) : this(Injekt.get<RideBusDatabase>().stopDao().getStop(stopId).firstOrNull())

    @Suppress("unused")
    constructor(bundle: Bundle) : this(bundle.getInt(STOP_EXTRA))

    var stop: Stop? = null
        private set

    init {
        setHasOptionsMenu(true)
    }

    /**
     * Adapter containing a list of routes on stop.
     */
    private var adapter: RoutesOnStopAdapter? = null

    override fun getTitle(): String {
        return "${stop?.name}"
    }

    override fun getSubtitle(): String {
        return "${stop?.direction}"
    }

    override fun createBinding(inflater: LayoutInflater) = StopsRouteControllerBinding.inflate(inflater)

    override fun createPresenter(): RouteOnStopPresenter {
        return RouteOnStopPresenter(stop!!)
    }

    override fun onViewCreated(view: View) {
        super.onViewCreated(view)

        binding.recycler.applyInsetter {
            type(navigationBars = true) {
                padding()
            }
        }

        val calendar: Calendar = Calendar.getInstance()
        val date: Date = calendar.time
        binding.dayOfWeek.text = SimpleDateFormat("EEEE", Locale.getDefault()).format(date.time)

        // Init RecyclerView and adapter
        adapter = RoutesOnStopAdapter(this)
        binding.recycler.adapter = adapter
        binding.recycler.layoutManager = LinearLayoutManager(view.context)
        binding.recycler.setHasFixedSize(true)
    }

    override fun onDestroyView(view: View) {
        adapter = null
        super.onDestroyView(view)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.stops_on_route, menu)
    }

    override fun onUpdateEmptyView(size: Int) {
        if (size > 0) {
            binding.emptyView.hide()
        } else {
            binding.emptyView.show(R.drawable.ic_alert, R.string.information_no_route_on_stop)
        }
    }

    fun onNextRoute(routesOnStop: List<RoutesOnStopItem>) {
        adapter?.updateDataSet(routesOnStop)
    }

    override fun onItemClick(view: View?, position: Int): Boolean {
        onItemClick(position)
        return false
    }

    override fun onItemClick(position: Int) {
        val route = adapter?.getItem(position)?.route?.routeId ?: return
        val stop = stop?.stopId ?: return
        router.pushController(ScheduleTabbedController(route, stop).withFadeTransaction())
    }

    companion object {
        const val STOP_EXTRA = "stop"
    }
}
