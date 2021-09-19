package org.xtimms.ridebus.ui.routes

import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import dev.chrisbanes.insetter.applyInsetter
import eu.davidea.flexibleadapter.FlexibleAdapter
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.xtimms.ridebus.R
import org.xtimms.ridebus.databinding.TransportControllerBinding
import org.xtimms.ridebus.ui.base.controller.RxController
import org.xtimms.ridebus.ui.base.controller.withFadeTransaction
import org.xtimms.ridebus.ui.details.RouteDetailsController
import org.xtimms.ridebus.ui.main.MainActivity
import org.xtimms.ridebus.util.view.onAnimationsFinished
import reactivecircus.flowbinding.appcompat.queryTextChanges

abstract class RouteController :
    RxController<TransportControllerBinding>(),
    FlexibleAdapter.OnItemClickListener,
    FlexibleAdapter.OnUpdateListener,
    RouteAdapter.OnItemClickListener {

    private var adapter: RouteAdapter? = null

    private var query = ""

    init {
        setHasOptionsMenu(true)
    }

    override fun createBinding(inflater: LayoutInflater) = TransportControllerBinding.inflate(inflater)

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
        adapter?.updateDataSet(getRoutes())
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

    abstract fun getRoutes(): List<RouteItem>

    private fun drawRoutes() {
        if (query.isNotBlank()) {
            adapter?.updateDataSet(
                getRoutes().filter {
                    it.route.title.contains(query, ignoreCase = true)
                }
            )
        } else {
            adapter?.updateDataSet(getRoutes())
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
}
