package org.xtimms.ridebus.ui.favourite

import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import dev.chrisbanes.insetter.applyInsetter
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.IFlexible
import org.xtimms.ridebus.R
import org.xtimms.ridebus.data.database.entity.Route
import org.xtimms.ridebus.data.preference.PreferencesHelper
import org.xtimms.ridebus.databinding.FavouriteControllerBinding
import org.xtimms.ridebus.ui.base.controller.NucleusController
import org.xtimms.ridebus.ui.base.controller.RootController
import org.xtimms.ridebus.ui.base.controller.withFadeTransaction
import org.xtimms.ridebus.ui.details.route.RouteDetailsController
import org.xtimms.ridebus.ui.main.MainActivity
import org.xtimms.ridebus.util.preference.minusAssign
import org.xtimms.ridebus.util.preference.plusAssign
import org.xtimms.ridebus.util.view.onAnimationsFinished
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class FavouritesController :
    NucleusController<FavouriteControllerBinding, FavouritesPresenter>(),
    RootController,
    FlexibleAdapter.OnItemClickListener,
    FlexibleAdapter.OnItemLongClickListener,
    FlexibleAdapter.OnUpdateListener,
    FavouritesAdapter.OnFavouriteItemClickListener {

    private val preferences: PreferencesHelper = Injekt.get()

    /**
     * Adapter containing sources.
     */
    private var adapter: FavouritesAdapter? = null

    override fun getTitle(): String? {
        return resources?.getString(R.string.title_favorite)
    }

    override fun createBinding(inflater: LayoutInflater) =
        FavouriteControllerBinding.inflate(inflater)

    override fun createPresenter(): FavouritesPresenter {
        return FavouritesPresenter()
    }

    override fun onViewCreated(view: View) {
        super.onViewCreated(view)

        binding.recycler.applyInsetter {
            type(navigationBars = true) {
                padding()
            }
        }

        adapter = FavouritesAdapter(this)

        binding.recycler.layoutManager = LinearLayoutManager(view.context)
        binding.recycler.adapter = adapter
        binding.recycler.onAnimationsFinished {
            (activity as? MainActivity)?.ready = true
        }
        adapter?.fastScroller = binding.fastScroller
    }

    override fun onDestroyView(view: View) {
        adapter = null
        super.onDestroyView(view)
    }

    override fun onUpdateEmptyView(size: Int) {
        if (size > 0) {
            binding.emptyView.hide()
        } else {
            binding.emptyView.show(
                R.drawable.ic_favorite_off,
                R.string.information_no_favorite_stops
            )
        }
    }

    override fun onItemClick(position: Int) {
        val item = adapter?.getItem(position) as? FavouriteItem ?: return
        val favourite = item.route
        openRouteDetails(RouteDetailsController(favourite))
    }

    override fun onPinClick(position: Int) {
        val item = adapter?.getItem(position) as? FavouriteItem ?: return
        toggleFavouritePin(item.route)
    }

    override fun onItemClick(view: View, position: Int): Boolean {
        onItemClick(position)
        return false
    }

    override fun onItemLongClick(position: Int) {
        val activity = activity ?: return
        val item = adapter?.getItem(position) as? FavouriteItem ?: return

        val isPinned = item.header?.type?.equals(FavouritesPresenter.PINNED_KEY) ?: false

        val items = mutableListOf(
            Pair(
                activity.getString(if (isPinned) R.string.action_unpin else R.string.action_pin),
                { toggleFavouritePin(item.route) }
            )
        )

        FavouriteOptionsDialog(item.route.title, items).showDialog(router)
    }

    private fun toggleFavouritePin(favourite: Route) {
        val isPinned = favourite.routeId.toString() in preferences.pinnedFavourites().get()
        if (isPinned) {
            preferences.pinnedFavourites() -= favourite.routeId.toString()
        } else {
            preferences.pinnedFavourites() += favourite.routeId.toString()
        }

        presenter.updateFavourites()
    }

    private fun openRouteDetails(controller: RouteDetailsController) {
        parentController!!.router.pushController(controller.withFadeTransaction())
    }

    fun setFavourites(list: List<IFlexible<*>>) {
        adapter?.updateDataSet(list)
    }
}
