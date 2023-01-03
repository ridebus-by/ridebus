package org.xtimms.ridebus.ui.routes.details

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.FloatRange
import androidx.core.view.doOnNextLayout
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bluelinelabs.conductor.ControllerChangeHandler
import com.bluelinelabs.conductor.ControllerChangeType
import dev.chrisbanes.insetter.applyInsetter
import eu.davidea.flexibleadapter.FlexibleAdapter
import logcat.LogPriority
import org.xtimms.ridebus.BuildConfig
import org.xtimms.ridebus.R
import org.xtimms.ridebus.data.database.RideBusDatabase
import org.xtimms.ridebus.data.database.entity.Route
import org.xtimms.ridebus.data.preference.PreferencesHelper
import org.xtimms.ridebus.databinding.RoutesDetailControllerBinding
import org.xtimms.ridebus.ui.base.controller.DialogController
import org.xtimms.ridebus.ui.base.controller.FabController
import org.xtimms.ridebus.ui.base.controller.NucleusController
import org.xtimms.ridebus.ui.base.controller.getMainAppBarHeight
import org.xtimms.ridebus.ui.base.controller.withFadeTransaction
import org.xtimms.ridebus.ui.main.MainActivity
import org.xtimms.ridebus.ui.routes.details.info.RouteInfoHeaderAdapter
import org.xtimms.ridebus.ui.routes.details.stop.RouteStopsHeaderAdapter
import org.xtimms.ridebus.ui.routes.details.stop.StopOnRouteItem
import org.xtimms.ridebus.ui.routes.details.stop.StopsOnRouteAdapter
import org.xtimms.ridebus.ui.routes.details.stop.base.BaseStopsAdapter
import org.xtimms.ridebus.ui.schedule.ScheduleTabbedController
import org.xtimms.ridebus.util.preference.minusAssign
import org.xtimms.ridebus.util.preference.plusAssign
import org.xtimms.ridebus.util.system.logcat
import org.xtimms.ridebus.util.system.toast
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get
import uy.kohesive.injekt.injectLazy
import kotlin.math.min

class RouteDetailsController :
    NucleusController<RoutesDetailControllerBinding, RouteDetailsPresenter>,
    FabController,
    FlexibleAdapter.OnItemClickListener,
    BaseStopsAdapter.OnStopClickListener {

    private val preferences: PreferencesHelper by injectLazy()

    constructor(route: Route?) : super(
        Bundle().apply {
            putInt(ROUTE_EXTRA, route?.routeId ?: 0)
        }
    ) {
        this.route = route
    }

    constructor(routeId: Int) : this(
        Injekt.get<RideBusDatabase>().routeDao().getRoute(routeId).firstOrNull()
    )

    @Suppress("unused")
    constructor(bundle: Bundle) : this(bundle.getInt(ROUTE_EXTRA))

    var route: Route? = null
        private set

    private var routeInfoAdapter: RouteInfoHeaderAdapter? = null
    private var stopsHeaderAdapter: RouteStopsHeaderAdapter? = null
    private var stopsAdapter: StopsOnRouteAdapter? = null

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
        return "${resources!!.getString(R.string.label_route)} №${route?.number}"
    }

    override fun onChangeStarted(handler: ControllerChangeHandler, type: ControllerChangeType) {
        super.onChangeStarted(handler, type)
        if (dialog == null) {
            updateToolbarTitleAlpha(if (type.isEnter) 0F else 1F)
        }
        recyclerViewUpdatesToolbarTitleAlpha(type.isEnter)
    }

    override fun onChangeEnded(changeHandler: ControllerChangeHandler, changeType: ControllerChangeType) {
        super.onChangeEnded(changeHandler, changeType)
        if (route == null) {
            activity?.toast(R.string.route_not_in_db)
            router.popController(this)
        }
    }

    override fun createPresenter(): RouteDetailsPresenter {
        return RouteDetailsPresenter(
            route!!
        )
    }

    override fun createBinding(inflater: LayoutInflater) = RoutesDetailControllerBinding.inflate(inflater)

    override fun onViewCreated(view: View) {
        super.onViewCreated(view)

        listOfNotNull(binding.fullRecycler, binding.infoRecycler, binding.stopsRecycler)
            .forEach {
                it.applyInsetter {
                    type(navigationBars = true) {
                        padding()
                    }
                }

                it.layoutManager = LinearLayoutManager(view.context)
                it.setHasFixedSize(true)
            }

        if (route == null) return

        // Init RecyclerView and adapter
        routeInfoAdapter = RouteInfoHeaderAdapter(this, binding.infoRecycler != null).apply {
            setHasStableIds(true)
        }
        stopsHeaderAdapter = RouteStopsHeaderAdapter(this).apply {
            setHasStableIds(true)
        }
        stopsAdapter = StopsOnRouteAdapter(this)

        // Phone layout
        binding.fullRecycler?.let {
            val config = ConcatAdapter.Config.Builder()
                .setIsolateViewTypes(true)
                .setStableIdMode(ConcatAdapter.Config.StableIdMode.SHARED_STABLE_IDS)
                .build()
            it.adapter = ConcatAdapter(config, routeInfoAdapter, stopsHeaderAdapter, stopsAdapter)

            binding.fastScroller.doOnNextLayout { scroller ->
                scroller.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    topMargin += getMainAppBarHeight()
                }
            }
        }

        // Tablet layout
        binding.infoRecycler?.adapter = routeInfoAdapter
        binding.stopsRecycler?.adapter = ConcatAdapter(stopsHeaderAdapter, stopsAdapter)

        stopsAdapter?.fastScroller = binding.fastScroller

        recyclerViewUpdatesToolbarTitleAlpha(true)
    }

    private fun recyclerViewUpdatesToolbarTitleAlpha(enable: Boolean) {
        val recycler = binding.fullRecycler ?: binding.infoRecycler ?: return
        if (enable) {
            if (!recyclerViewToolbarTitleAlphaUpdaterAdded) {
                recycler.addOnScrollListener(recyclerViewToolbarTitleAlphaUpdater)
                recyclerViewToolbarTitleAlphaUpdaterAdded = true
            }
        } else if (recyclerViewToolbarTitleAlphaUpdaterAdded) {
            recycler.removeOnScrollListener(recyclerViewToolbarTitleAlphaUpdater)
            recyclerViewToolbarTitleAlphaUpdaterAdded = false
        }
    }

    private fun updateToolbarTitleAlpha(@FloatRange(from = 0.0, to = 1.0) alpha: Float? = null) {
        // Controller may actually already be destroyed by the time this gets run
        if (!isAttached) return

        val scrolledList = binding.fullRecycler ?: binding.infoRecycler!!
        (activity as? MainActivity)?.binding?.appbar?.titleTextAlpha = when {
            // Specific alpha provided
            alpha != null -> alpha

            // First item isn't in view, full opacity
            ((scrolledList.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition() > 0) -> 1F

            // Based on scroll amount when first item is in view
            else -> min(scrolledList.computeVerticalScrollOffset(), 255) / 255F
        }
    }

    override fun onDestroyView(view: View) {
        recyclerViewUpdatesToolbarTitleAlpha(false)
        routeInfoAdapter = null
        stopsHeaderAdapter = null
        stopsAdapter = null
        super.onDestroyView(view)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.route_info, menu)
        menu.findItem(R.id.action_favourite).icon?.mutate()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_favourite -> addToFavourites(route!!)
            R.id.action_report -> report()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val isAdded = route?.routeId.toString() in preferences.favourites().get()
        menu.findItem(R.id.action_favourite).setIcon(
            if (isAdded) R.drawable.ic_favourite_filled else R.drawable.ic_favourite
        )
    }

    private fun addToFavourites(route: Route) {
        val isAdded = route.routeId.toString() in preferences.favourites().get()
        if (isAdded) {
            preferences.favourites() -= route.routeId.toString()
        } else {
            preferences.favourites() += route.routeId.toString()
        }
        activity?.invalidateOptionsMenu()
    }

    private fun report() {
        val intent = Intent(Intent.ACTION_SENDTO)

        intent.putExtra(Intent.EXTRA_SUBJECT, resources!!.getString(R.string.email_subject))
        intent.data = Uri.parse(BuildConfig.DEVELOPER_EMAIL)

        try {
            startActivity(intent)
        } catch (e: Exception) {
            logcat(LogPriority.ERROR, e)
            activity?.toast(e.localizedMessage)
        }
    }

    fun onNextStops(stops: List<StopOnRouteItem>) {
        val stopsHeader = stopsHeaderAdapter ?: return
        stopsHeader.setNumStops(stops.size)

        val adapter = stopsAdapter ?: return
        adapter.updateDataSet(stops)
    }

    override fun onStopClick(position: Int) {
        val adapter = stopsAdapter
        val route = route?.routeId ?: return
        val stop = adapter?.getItem(position)?.stop?.stopId ?: return
        router.pushController(ScheduleTabbedController(route, stop).withFadeTransaction())
    }

    fun showAttentionDialog() {
        activity?.let {
            AttentionDialog(this).showDialog(router)
        }
        preferences.isVisibleAttentionNote().set(false)
    }

    override fun onItemClick(view: View?, position: Int): Boolean {
        onStopClick(position)
        return false
    }

    companion object {
        const val ROUTE_EXTRA = "route"
    }
}
