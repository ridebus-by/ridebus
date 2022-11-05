package org.xtimms.ridebus.ui.routes

import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.isVisible
import com.bluelinelabs.conductor.*
import com.bluelinelabs.conductor.viewpager.RouterPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.jakewharton.rxrelay.BehaviorRelay
import logcat.LogPriority
import org.xtimms.ridebus.R
import org.xtimms.ridebus.data.database.RideBusDatabase
import org.xtimms.ridebus.data.preference.PreferencesHelper
import org.xtimms.ridebus.data.updater.database.DatabaseUpdateChecker
import org.xtimms.ridebus.data.updater.database.DatabaseUpdateResult
import org.xtimms.ridebus.databinding.PagerControllerBinding
import org.xtimms.ridebus.ui.base.controller.RootController
import org.xtimms.ridebus.ui.base.controller.RxController
import org.xtimms.ridebus.ui.base.controller.TabbedController
import org.xtimms.ridebus.ui.main.MainActivity
import org.xtimms.ridebus.ui.more.NewScheduleDialogController
import org.xtimms.ridebus.util.lang.launchNow
import org.xtimms.ridebus.util.system.logcat
import org.xtimms.ridebus.util.system.toast
import rx.Subscription
import uy.kohesive.injekt.injectLazy

class RoutesTabbedController :
    RxController<PagerControllerBinding>(),
    RootController,
    TabbedController {

    private val db: RideBusDatabase by injectLazy()
    private val preferences: PreferencesHelper by injectLazy()
    private val updateChecker by lazy { DatabaseUpdateChecker() }

    private var tabsVisibilityRelay: BehaviorRelay<Boolean> = BehaviorRelay.create(false)

    private var tabsVisibilitySubscription: Subscription? = null

    private val typesOfTransport: List<Int>
        get() = db.transportDao().getTypesOfTransportPerCity(preferences.city().get().ordinal)

    private var adapter: RoutesAdapter? = null

    init {
        setHasOptionsMenu(true)
        retainViewMode = RetainViewMode.RETAIN_DETACH
    }

    override fun getTitle(): String {
        return resources!!.getString(R.string.title_routes)
    }

    override fun createBinding(inflater: LayoutInflater) = PagerControllerBinding.inflate(inflater)

    override fun onViewCreated(view: View) {
        super.onViewCreated(view)

        adapter = RoutesAdapter()
        binding.pager.adapter = adapter

        tabsVisibilityRelay.call((adapter?.count ?: 0) > 1)
    }

    override fun onDestroyView(view: View) {
        adapter = null
        tabsVisibilitySubscription?.unsubscribe()
        tabsVisibilitySubscription = null
        super.onDestroyView(view)
    }

    override fun onChangeStarted(handler: ControllerChangeHandler, type: ControllerChangeType) {
        super.onChangeStarted(handler, type)
        if (type.isEnter) {
            (activity as? MainActivity)?.binding?.tabs?.apply {
                setupWithViewPager(binding.pager)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.tabbed_routes, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_update -> checkDatabaseFromServer()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun configureTabs(tabs: TabLayout): Boolean {
        with(tabs) {
            isVisible = false
            tabGravity = TabLayout.GRAVITY_FILL
            tabMode = TabLayout.MODE_SCROLLABLE
        }
        tabsVisibilitySubscription?.unsubscribe()
        tabsVisibilitySubscription = tabsVisibilityRelay.subscribe { visible ->
            tabs.isVisible = visible
        }

        return false
    }

    override fun cleanupTabs(tabs: TabLayout) {
        tabsVisibilitySubscription?.unsubscribe()
        tabsVisibilitySubscription = null
    }

    private fun checkDatabaseFromServer() {
        if (activity == null) return

        activity!!.toast(R.string.update_check_look_for_updates)

        launchNow {
            try {
                when (val result = updateChecker.checkForUpdate(activity!!, isUserPrompt = true)) {
                    is DatabaseUpdateResult.NewUpdate -> {
                        NewScheduleDialogController(result).showDialog(router)
                    }
                    is DatabaseUpdateResult.NoNewUpdate -> {
                        activity?.toast(R.string.update_check_no_new_updates)
                    }
                }
            } catch (error: Exception) {
                activity?.toast(error.message)
                logcat(LogPriority.ERROR, error)
            }
        }
    }

    private inner class RoutesAdapter : RouterPagerAdapter(this@RoutesTabbedController) {

        private val tabTitles = typesOfTransport.map {
            when (it) {
                1 -> R.string.label_bus
                2 -> R.string.label_route_taxi
                3 -> R.string.label_express
                4 -> R.string.label_tram
                else -> R.string.unknown
            }
        }.map { resources!!.getString(it) }

        override fun getCount(): Int {
            return db.transportDao().getTypesOfTransportPerCity(preferences.city().get().ordinal).size
        }

        override fun configureRouter(router: Router, position: Int) {
            if (!router.hasRootController()) {
                val controller: Controller = when (position) {
                    BUS_CONTROLLER -> BusController()
                    ROUTE_TAXI_CONTROLLER -> TaxiController()
                    EXPRESS_CONTROLLER -> ExpressController()
                    TRAM_CONTROLLER -> TramController()
                    else -> error("Wrong position $position")
                }
                router.setRoot(RouterTransaction.with(controller))
            }
        }

        override fun getPageTitle(position: Int): CharSequence {
            return tabTitles[position]
        }
    }

    companion object {
        const val BUS_CONTROLLER = 0
        const val ROUTE_TAXI_CONTROLLER = 1
        const val EXPRESS_CONTROLLER = 2
        const val TRAM_CONTROLLER = 3
    }
}
