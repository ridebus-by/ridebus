package org.xtimms.ridebus.ui.stops

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
import org.xtimms.ridebus.databinding.StopsControllerBinding
import org.xtimms.ridebus.ui.base.controller.NucleusController
import org.xtimms.ridebus.ui.base.controller.RootController
import org.xtimms.ridebus.ui.base.controller.withFadeTransaction
import org.xtimms.ridebus.ui.main.MainActivity
import org.xtimms.ridebus.ui.stub.StubController
import org.xtimms.ridebus.util.view.onAnimationsFinished
import reactivecircus.flowbinding.appcompat.queryTextChanges
import uy.kohesive.injekt.injectLazy

class StopsController :
    NucleusController<StopsControllerBinding, StopsPresenter>(),
    RootController,
    FlexibleAdapter.OnUpdateListener,
    StopsAdapter.OnItemClickListener {

    private val db: RideBusDatabase by injectLazy()

    private var adapter: StopsAdapter? = null

    private val items = db.stopDao().getAll().map { StopsItem(it) } // TODO Rx

    private var query = ""

    init {
        setHasOptionsMenu(true)
    }

    override fun getTitle(): String? {
        return resources?.getString(R.string.title_stops)
    }

    override fun createBinding(inflater: LayoutInflater) = StopsControllerBinding.inflate(inflater)

    override fun createPresenter(): StopsPresenter {
        return StopsPresenter()
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
            .filter { router.backstack.lastOrNull()?.controller == this }
            .onEach {
                query = it.toString()
                drawStops()
            }
            .launchIn(viewScope)
    }

    private fun drawStops() {
        if (query.isNotBlank()) {
            adapter?.updateDataSet(
                items.filter {
                    it.stop.name.contains(query, ignoreCase = true)
                }
            )
        } else {
            adapter?.updateDataSet(items)
        }
    }

    override fun onItemClick(position: Int) {
        router.pushController(StubController().withFadeTransaction())
    }

    override fun onUpdateEmptyView(size: Int) {
        if (size > 0) {
            binding.emptyView.hide()
        } else {
            binding.emptyView.show(R.drawable.ic_alert, R.string.information_no_stops)
        }
    }
}
