package org.xtimms.ridebus.ui.routes.bus

import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import dev.chrisbanes.insetter.applyInsetter
import eu.davidea.flexibleadapter.FlexibleAdapter
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.xtimms.ridebus.R
import org.xtimms.ridebus.data.database.Route
import org.xtimms.ridebus.databinding.BusControllerBinding
import org.xtimms.ridebus.ui.base.controller.NucleusController
import org.xtimms.ridebus.ui.base.controller.withFadeTransaction
import org.xtimms.ridebus.ui.routes.bus.details.BusDetailsController
import reactivecircus.flowbinding.appcompat.queryTextChanges

open class BusController :
    NucleusController<BusControllerBinding, BusPresenter>(),
    FlexibleAdapter.OnItemClickListener {

    var adapter: BusAdapter? = null
        private set

    private var query = ""

    init {
        setHasOptionsMenu(true)
    }

    override fun createPresenter(): BusPresenter {
        return BusPresenter()
    }

    override fun createBinding(inflater: LayoutInflater) = BusControllerBinding.inflate(inflater)

    override fun onViewCreated(view: View) {
        super.onViewCreated(view)

        binding.recycler.applyInsetter {
            type(navigationBars = true) {
                padding()
            }
        }

        binding.recycler.layoutManager = LinearLayoutManager(view.context)
        adapter = BusAdapter(this@BusController)
        binding.recycler.setHasFixedSize(true)
        binding.recycler.adapter = adapter
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
        inflater.inflate(R.menu.routes_buses, menu)

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
            .filter { router.backstack.lastOrNull()?.controller() == this }
            .onEach {
                query = it.toString()
            }
            .launchIn(viewScope)
    }

    override fun onItemClick(view: View?, position: Int): Boolean {
        val bus = (adapter?.getItem(position) as? BusItem)?.route ?: return false
        openDetails(bus)
        return false
    }

    private fun openDetails(route: Route) {
        val controller = BusDetailsController()
        parentController!!.router.pushController(controller.withFadeTransaction())
    }

}