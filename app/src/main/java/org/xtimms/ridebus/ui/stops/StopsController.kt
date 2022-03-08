package org.xtimms.ridebus.ui.stops

import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import dev.chrisbanes.insetter.applyInsetter
import eu.davidea.flexibleadapter.FlexibleAdapter
import kotlinx.coroutines.flow.*
import org.xtimms.ridebus.R
import org.xtimms.ridebus.data.preference.PreferencesHelper
import org.xtimms.ridebus.databinding.StopsControllerBinding
import org.xtimms.ridebus.ui.base.controller.NucleusController
import org.xtimms.ridebus.ui.base.controller.RootController
import org.xtimms.ridebus.ui.base.controller.withFadeTransaction
import org.xtimms.ridebus.ui.main.MainActivity
import org.xtimms.ridebus.ui.stops.details.RoutesOnStopController
import org.xtimms.ridebus.util.view.onAnimationsFinished
import reactivecircus.flowbinding.appcompat.queryTextChanges
import uy.kohesive.injekt.injectLazy

class StopsController :
    NucleusController<StopsControllerBinding, StopsPresenter>(),
    RootController,
    FlexibleAdapter.OnItemClickListener,
    FlexibleAdapter.OnUpdateListener,
    StopsAdapter.OnItemClickListener {

    private val preferences: PreferencesHelper by injectLazy()

    private var adapter: StopsAdapter? = null

    private var stops: List<StopsItem> = emptyList()

    private var query = ""

    init {
        setHasOptionsMenu(true)
    }

    override fun getTitle(): String? {
        return resources?.getString(R.string.title_stops)
    }

    override fun createBinding(inflater: LayoutInflater) = StopsControllerBinding.inflate(inflater)

    override fun createPresenter(): StopsPresenter {
        return StopsPresenter(preferences.city().get().ordinal)
    }

    override fun onViewCreated(view: View) {
        super.onViewCreated(view)

        binding.recycler.applyInsetter {
            type(navigationBars = true) {
                padding()
            }
        }

        adapter = StopsAdapter(this)

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
        inflater.inflate(R.menu.stops, menu)

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
            .drop(1)
            .filter { router.backstack.lastOrNull()?.controller == this }
            .onEach {
                query = it.toString()
                updateStopsList()
            }
            .launchIn(viewScope)
    }

    fun setStops(stops: List<StopsItem>) {
        this.stops = stops
        updateStopsList()
    }

    private fun updateStopsList() {
        if (query.isNotBlank()) {
            adapter?.updateDataSet(
                stops.filter {
                    it.stop.name.contains(query, ignoreCase = true)
                }
            )
        } else {
            adapter?.updateDataSet(stops)
        }
    }

    override fun onItemClick(position: Int) {
        val stop = (adapter?.getItem(position) as? StopsItem)?.stop?.stopId ?: return
        router.pushController(RoutesOnStopController(stop).withFadeTransaction())
    }

    override fun onUpdateEmptyView(size: Int) {
        if (size > 0) {
            binding.emptyView.hide()
        } else {
            binding.emptyView.show(R.drawable.ic_alert, R.string.information_no_stops)
        }
    }

    override fun onItemClick(view: View?, position: Int): Boolean {
        onItemClick(position)
        return false
    }
}
