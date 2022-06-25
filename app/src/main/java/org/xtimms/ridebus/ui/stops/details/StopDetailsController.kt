package org.xtimms.ridebus.ui.stops.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.annotation.FloatRange
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bluelinelabs.conductor.ControllerChangeHandler
import com.bluelinelabs.conductor.ControllerChangeType
import eu.davidea.flexibleadapter.FlexibleAdapter
import org.xtimms.ridebus.R
import org.xtimms.ridebus.data.database.RideBusDatabase
import org.xtimms.ridebus.data.database.entity.Stop
import org.xtimms.ridebus.databinding.StopsRouteControllerBinding
import org.xtimms.ridebus.ui.base.controller.DialogController
import org.xtimms.ridebus.ui.base.controller.NoAppBarElevationController
import org.xtimms.ridebus.ui.base.controller.NucleusController
import org.xtimms.ridebus.ui.base.controller.withFadeTransaction
import org.xtimms.ridebus.ui.schedule.ScheduleTabbedController
import org.xtimms.ridebus.ui.stops.details.info.StopInfoHeaderAdapter
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class StopDetailsController :
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

    private var stopInfoAdapter: StopInfoHeaderAdapter? = null
    private var routesAdapter: RoutesOnStopAdapter? = null

    private var dialog: DialogController? = null

    /**
     * For [recyclerViewUpdatesToolbarTitleAlpha]
     */
    private var recyclerViewToolbarTitleAlphaUpdaterAdded = false
    private val recyclerViewToolbarTitleAlphaUpdater = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            updateToolbarTitleAlpha()
        }
    }

    init {
        setHasOptionsMenu(true)
    }

    override fun getTitle(): String {
        return "${stop?.name}"
    }

    override fun onChangeStarted(handler: ControllerChangeHandler, type: ControllerChangeType) {
        super.onChangeStarted(handler, type)
        if (dialog == null) {
            updateToolbarTitleAlpha(if (type.isEnter) 0F else 1F)
        }
        recyclerViewUpdatesToolbarTitleAlpha(type.isEnter)
    }

    override fun createBinding(inflater: LayoutInflater) = StopsRouteControllerBinding.inflate(inflater)

    override fun createPresenter(): RouteOnStopPresenter {
        return RouteOnStopPresenter(stop!!)
    }

    override fun onViewCreated(view: View) {
        super.onViewCreated(view)

        // Init RecyclerView and adapter
        routesAdapter = RoutesOnStopAdapter(this)
        binding.recycler.adapter = routesAdapter
        binding.recycler.layoutManager = LinearLayoutManager(view.context)
        binding.recycler.setHasFixedSize(true)
    }

    override fun onDestroyView(view: View) {
        routesAdapter = null
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

    fun onNextStop(routesOnStop: List<RoutesOnStopItem>) {
        routesAdapter?.updateDataSet(routesOnStop)
    }

    override fun onItemClick(view: View?, position: Int): Boolean {
        onItemClick(position)
        return false
    }

    override fun onItemClick(position: Int) {
        val route = routesAdapter?.getItem(position)?.route?.routeId ?: return
        val stop = stop?.stopId ?: return
        router.pushController(ScheduleTabbedController(route, stop).withFadeTransaction())
    }

    private fun recyclerViewUpdatesToolbarTitleAlpha(enable: Boolean) {
    }

    private fun updateToolbarTitleAlpha(@FloatRange(from = 0.0, to = 1.0) alpha: Float? = null) {
    }

    companion object {
        const val STOP_EXTRA = "stop"
    }
}
