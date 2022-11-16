package org.xtimms.ridebus.ui.stops.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import dev.chrisbanes.insetter.applyInsetter
import eu.davidea.flexibleadapter.FlexibleAdapter
import logcat.LogPriority
import org.xtimms.ridebus.R
import org.xtimms.ridebus.data.database.RideBusDatabase
import org.xtimms.ridebus.data.database.entity.Stop
import org.xtimms.ridebus.databinding.StopsRouteControllerBinding
import org.xtimms.ridebus.ui.base.controller.NoAppBarElevationController
import org.xtimms.ridebus.ui.base.controller.NucleusController
import org.xtimms.ridebus.ui.base.controller.withFadeTransaction
import org.xtimms.ridebus.ui.schedule.ScheduleTabbedController
import org.xtimms.ridebus.util.system.logcat
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get
import java.text.SimpleDateFormat
import java.util.*

class RoutesOnStopController :
    NucleusController<StopsRouteControllerBinding, RouteOnStopPresenter>,
    NoAppBarElevationController,
    FlexibleAdapter.OnItemClickListener,
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

        binding.progress.isVisible = true
    }

    override fun onDestroyView(view: View) {
        adapter = null
        super.onDestroyView(view)
    }

    fun onNextRoute(routesOnStop: List<RoutesOnStopItem>) {
        val adapter = adapter ?: return
        hideProgressBar()
        adapter.updateDataSet(routesOnStop)
    }

    fun onNextRouteError(error: Throwable) {
        logcat(LogPriority.ERROR, error)
        val adapter = adapter ?: return
        adapter.onLoadMoreComplete(null)
        hideProgressBar()

        if (adapter.isEmpty) {
            binding.emptyView.show(R.drawable.ic_alert, R.string.information_no_route_on_stop)
        }
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

    /**
     * Hides active progress bars.
     */
    private fun hideProgressBar() {
        binding.emptyView.hide()
        binding.progress.isVisible = false
    }

    companion object {
        const val STOP_EXTRA = "stop"
    }
}
