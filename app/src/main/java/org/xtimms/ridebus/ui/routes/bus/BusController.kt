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
import org.xtimms.ridebus.data.database.RideBusDatabase
import org.xtimms.ridebus.data.preference.PreferencesHelper
import org.xtimms.ridebus.databinding.BusControllerBinding
import org.xtimms.ridebus.ui.base.controller.NucleusController
import org.xtimms.ridebus.ui.main.MainActivity
import org.xtimms.ridebus.util.view.onAnimationsFinished
import reactivecircus.flowbinding.appcompat.queryTextChanges
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

open class BusController(
    db: RideBusDatabase = Injekt.get(),
    preferences: PreferencesHelper = Injekt.get()
) :
    NucleusController<BusControllerBinding, BusPresenter>(),
    FlexibleAdapter.OnItemClickListener {

    private var adapter: BusAdapter? = null

    private val items = db.routeDao().getBuses(preferences.city().get().ordinal).map { BusItem(it) } // TODO Rx

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

        adapter = BusAdapter(this)

        binding.recycler.layoutManager = LinearLayoutManager(view.context)
        binding.recycler.adapter = adapter
        adapter?.updateDataSet(items)
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
                drawBuses()
            }
            .launchIn(viewScope)
    }

    override fun onItemClick(view: View?, position: Int): Boolean {
        return false
    }

    private fun drawBuses() {
        if (query.isNotBlank()) {
            adapter?.updateDataSet(
                items.filter {
                    it.route.description.contains(query, ignoreCase = true)
                }
            )
        } else {
            adapter?.updateDataSet(items)
        }
    }
}
