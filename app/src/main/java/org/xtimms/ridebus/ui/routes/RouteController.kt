package org.xtimms.ridebus.ui.routes

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import dev.chrisbanes.insetter.applyInsetter
import eu.davidea.flexibleadapter.FlexibleAdapter
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.xtimms.ridebus.R
import org.xtimms.ridebus.data.database.RideBusDatabase
import org.xtimms.ridebus.data.database.entity.Route
import org.xtimms.ridebus.data.preference.PreferencesHelper
import org.xtimms.ridebus.databinding.TransportControllerBinding
import org.xtimms.ridebus.ui.base.controller.NucleusController
import org.xtimms.ridebus.ui.base.controller.withFadeTransaction
import org.xtimms.ridebus.ui.main.MainActivity
import org.xtimms.ridebus.ui.routes.details.RouteDetailsController
import org.xtimms.ridebus.util.view.onAnimationsFinished
import reactivecircus.flowbinding.appcompat.queryTextChanges
import uy.kohesive.injekt.injectLazy

class RouteController :
    NucleusController<TransportControllerBinding, RoutePresenter>,
    FlexibleAdapter.OnItemClickListener,
    FlexibleAdapter.OnUpdateListener,
    RouteAdapter.OnItemClickListener {

    constructor(transportType: Int) : super(
        Bundle().apply {
            putInt(TRANSPORT_TYPE_EXTRA, transportType)
        }
    ) {
        this.transportType = transportType
    }

    @Suppress("unused")
    constructor(bundle: Bundle) : this(
        bundle.getInt(TRANSPORT_TYPE_EXTRA)
    )

    val db: RideBusDatabase by injectLazy()
    val preferences: PreferencesHelper by injectLazy()

    private var adapter: RouteAdapter? = null

    private var routes: List<RouteItem> = emptyList()

    private var query = ""

    var transportType: Int? = null
        private set

    lateinit var route: Route
        private set

    init {
        setHasOptionsMenu(true)
    }

    override fun createBinding(inflater: LayoutInflater) = TransportControllerBinding.inflate(inflater)

    override fun createPresenter(): RoutePresenter {
        return RoutePresenter(transportType!!)
    }

    override fun onViewCreated(view: View) {
        super.onViewCreated(view)

        binding.recycler.applyInsetter {
            type(navigationBars = true) {
                padding()
            }
        }

        adapter = RouteAdapter(this)

        binding.recycler.layoutManager = LinearLayoutManager(view.context)
        binding.recycler.adapter = adapter
        binding.recycler.onAnimationsFinished {
            (activity as? MainActivity)?.ready = true
        }
        binding.recycler.setHasFixedSize(true)
        adapter?.fastScroller = binding.fastScroller
    }

    override fun onDestroyView(view: View) {
        adapter = null
        super.onDestroyView(view)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_search -> expandActionViewFromInteraction = true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.routes, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        searchView.maxWidth = Int.MAX_VALUE

        searchItem.fixExpand(onExpand = { invalidateMenuOnExpand() })

        if (query.isNotEmpty()) {
            searchItem.expandActionView()
            searchView.setQuery(query, true)
            searchView.clearFocus()
        }

        searchView.queryTextChanges()
            .filter { router.backstack.lastOrNull()?.controller == this }
            .onEach {
                query = it.toString()
                drawRoutes()
            }
            .launchIn(viewScope)
    }

    fun setRoutes(routes: List<RouteItem>) {
        this.routes = routes
        drawRoutes()
    }

    private fun drawRoutes() {
        if (query.isNotBlank()) {
            adapter?.updateDataSet(
                routes.filter {
                    it.route.title.contains(query, ignoreCase = true)
                }
            )
        } else {
            adapter?.updateDataSet(routes)
        }
    }

    override fun onItemClick(position: Int) {
        val route = (adapter?.getItem(position) as? RouteItem)?.route?.routeId ?: return
        parentController!!.router.pushController(RouteDetailsController(route).withFadeTransaction())
    }

    override fun onUpdateEmptyView(size: Int) {
        if (size > 0) {
            binding.emptyView.hide()
        } else {
            binding.emptyView.show(R.drawable.ic_bus_alert, R.string.information_no_routes)
        }
    }

    override fun onItemClick(view: View?, position: Int): Boolean {
        onItemClick(position)
        return false
    }

    companion object {
        const val TRANSPORT_TYPE_EXTRA = "transportType"
    }
}
