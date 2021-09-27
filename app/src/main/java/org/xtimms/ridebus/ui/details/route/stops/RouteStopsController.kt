package org.xtimms.ridebus.ui.details.route.stops

import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import dev.chrisbanes.insetter.applyInsetter
import eu.davidea.flexibleadapter.FlexibleAdapter
import org.xtimms.ridebus.R
import org.xtimms.ridebus.data.database.RideBusDatabase
import org.xtimms.ridebus.data.database.entity.Route
import org.xtimms.ridebus.databinding.RouteStopsControllerBinding
import org.xtimms.ridebus.ui.base.controller.NucleusController
import org.xtimms.ridebus.ui.base.controller.withFadeTransaction
import org.xtimms.ridebus.ui.details.route.RouteDetailsController
import org.xtimms.ridebus.ui.stub.StubController
import uy.kohesive.injekt.injectLazy

class RouteStopsController :
    NucleusController<RouteStopsControllerBinding, RouteStopsPresenter>(),
    FlexibleAdapter.OnItemClickListener,
    FlexibleAdapter.OnUpdateListener,
    RouteStopsAdapter.OnItemClickListener {

    private val db: RideBusDatabase by injectLazy()

    /**
     * Adapter containing a list of stops on route.
     */
    private var adapter: RouteStopsAdapter? = null

    override fun createBinding(inflater: LayoutInflater) = RouteStopsControllerBinding.inflate(inflater)

    override fun createPresenter(): RouteStopsPresenter {
        val ctrl = parentController as RouteDetailsController
        return RouteStopsPresenter(ctrl.route!!)
    }

    override fun onViewCreated(view: View) {
        super.onViewCreated(view)

        binding.recycler.applyInsetter {
            type(navigationBars = true) {
                padding()
            }
        }

        // Init RecyclerView and adapter
        adapter = RouteStopsAdapter(this)
        binding.recycler.adapter = adapter
        binding.recycler.layoutManager = LinearLayoutManager(view.context)
        binding.recycler.setHasFixedSize(true)
    }

    override fun onDestroyView(view: View) {
        adapter = null
        super.onDestroyView(view)
    }

    override fun onItemClick(position: Int) {
        parentController!!.router.pushController(StubController().withFadeTransaction())
    }

    override fun onItemClick(view: View?, position: Int): Boolean {
        onItemClick(position)
        return false
    }

    /**
     * Update view with stops
     *
     * @param route route object containing information about route.
     */
    fun onNextRoute(route: Route) {
        val items = db.routesAndStopsDao().getStopsByRoute(route.routeId).map { RouteStopsItem(it) }
        adapter?.updateDataSet(items)
    }

    override fun onUpdateEmptyView(size: Int) {
        if (size > 0) {
            binding.emptyView.hide()
        } else {
            binding.emptyView.show(R.drawable.ic_alert, R.string.information_no_stops_on_route)
        }
    }
}
