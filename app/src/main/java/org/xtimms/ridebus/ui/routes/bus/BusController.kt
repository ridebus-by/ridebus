package org.xtimms.ridebus.ui.routes.bus

import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import dev.chrisbanes.insetter.applyInsetter
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.IFlexible
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.xtimms.ridebus.R
import org.xtimms.ridebus.data.database.Bus
import org.xtimms.ridebus.databinding.BusControllerBinding
import org.xtimms.ridebus.ui.base.controller.RxController
import org.xtimms.ridebus.ui.base.controller.withFadeTransaction
import org.xtimms.ridebus.ui.routes.bus.details.BusDetailsController
import reactivecircus.flowbinding.appcompat.queryTextChanges

open class BusController :
    RxController<BusControllerBinding>(),
    FlexibleAdapter.OnItemClickListener {

    private var adapter: FlexibleAdapter<IFlexible<*>>? = null

    private var query = ""

    init {
        setHasOptionsMenu(true)
    }

    override fun getTitle(): String? {
        return applicationContext?.getString(R.string.label_bus)
    }

    override fun createBinding(inflater: LayoutInflater) = BusControllerBinding.inflate(inflater)

    override fun onViewCreated(view: View) {
        super.onViewCreated(view)

        binding.recycler.applyInsetter {
            type(navigationBars = true) {
                padding()
            }
        }

        binding.emptyView.show("Oops... Nothing works.")

        adapter = BusAdapter(this)
        binding.recycler.layoutManager = LinearLayoutManager(view.context)
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
        val bus = (adapter?.getItem(position) as? BusItem)?.bus ?: return false
        openDetails(bus)
        return false
    }

    private fun openDetails(bus: Bus) {
        val controller = BusDetailsController()
        parentController!!.router.pushController(controller.withFadeTransaction())
    }

}