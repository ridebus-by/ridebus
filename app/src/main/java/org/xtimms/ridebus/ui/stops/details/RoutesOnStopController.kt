package org.xtimms.ridebus.ui.stops.details

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import dev.chrisbanes.insetter.applyInsetter
import eu.davidea.flexibleadapter.FlexibleAdapter
import kotlinx.coroutines.runBlocking
import logcat.LogPriority
import org.xtimms.ridebus.R
import org.xtimms.ridebus.data.model.Stop
import org.xtimms.ridebus.data.usecases.UseCases
import org.xtimms.ridebus.databinding.RoutesOnStopControllerBinding
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
    NucleusController<RoutesOnStopControllerBinding, RouteOnStopPresenter>,
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

    constructor(stopId: Int) : this(
        runBlocking { Injekt.get<UseCases>().getStop(stopId) }
    )

    @Suppress("unused")
    constructor(bundle: Bundle) : this(bundle.getInt(STOP_EXTRA))

    var stop: Stop? = null
        private set

    /**
     * Adapter containing a list of routes on stop.
     */
    private var adapter: RoutesOnStopAdapter? = null

    private val timeReceiver = TimeReceiver()

    override fun getTitle(): String {
        return "${stop?.name}"
    }

    override fun getSubtitle(): String {
        return "${stop?.direction}"
    }

    override fun createBinding(inflater: LayoutInflater) = RoutesOnStopControllerBinding.inflate(
        inflater
    )

    override fun createPresenter(): RouteOnStopPresenter {
        return RouteOnStopPresenter(checkNotNull(stop))
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
        adapter.updateDataSet(routesOnStop, false)
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

    override fun onAttach(view: View) {
        super.onAttach(view)
        view.context.registerReceiver(timeReceiver, IntentFilter(Intent.ACTION_TIME_TICK))
        adapter?.notifyDataSetChanged()
    }

    override fun onDetach(view: View) {
        view.context.registerReceiver(timeReceiver, IntentFilter(Intent.ACTION_TIME_TICK))
        super.onDetach(view)
    }

    private inner class TimeReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            adapter?.notifyDataSetChanged()
        }
    }

    companion object {
        const val STOP_EXTRA = "stop"
    }
}
