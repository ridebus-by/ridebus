package org.xtimms.ridebus.ui.routes

import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import com.bluelinelabs.conductor.ControllerChangeHandler
import com.bluelinelabs.conductor.ControllerChangeType
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.viewpager.RouterPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.jakewharton.rxrelay.BehaviorRelay
import kotlinx.coroutines.runBlocking
import org.xtimms.ridebus.R
import org.xtimms.ridebus.data.database.RideBusDatabase
import org.xtimms.ridebus.data.preference.PreferencesHelper
import org.xtimms.ridebus.data.usecases.UseCases
import org.xtimms.ridebus.databinding.PagerControllerBinding
import org.xtimms.ridebus.ui.base.controller.BaseController
import org.xtimms.ridebus.ui.base.controller.RootController
import org.xtimms.ridebus.ui.base.controller.TabbedController
import org.xtimms.ridebus.ui.main.MainActivity
import rx.Subscription
import uy.kohesive.injekt.injectLazy

class RoutesTabbedController :
    BaseController<PagerControllerBinding>(),
    RootController,
    TabbedController {

    private val db: RideBusDatabase by injectLazy()
    private val useCases: UseCases by injectLazy()
    private val preferences: PreferencesHelper by injectLazy()

    private var tabsVisibilityRelay: BehaviorRelay<Boolean> = BehaviorRelay.create(false)

    private var tabsVisibilitySubscription: Subscription? = null

    private val typesOfTransport: List<Int>
        get() = runBlocking { useCases.getTransportTypesPerCity(preferences.city().get().toInt()) }

    private var adapter: RoutesPagerAdapter? = null

    init {
        setHasOptionsMenu(true)
        retainViewMode = RetainViewMode.RETAIN_DETACH
    }

    override fun getTitle(): String {
        return checkNotNull(resources).getString(R.string.title_routes)
    }

    override fun createBinding(inflater: LayoutInflater) = PagerControllerBinding.inflate(inflater)

    override fun onViewCreated(view: View) {
        super.onViewCreated(view)

        adapter = RoutesPagerAdapter()
        binding.pager.adapter = adapter

        tabsVisibilityRelay.call((adapter?.count ?: 0) > 1)
    }

    override fun onDestroyView(view: View) {
        if (!checkNotNull(activity).isChangingConfigurations) {
            binding.pager.adapter = null
        }
        (activity as? MainActivity)?.binding?.tabs?.setupWithViewPager(null)
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

    private inner class RoutesPagerAdapter : RouterPagerAdapter(this@RoutesTabbedController) {

        private val tabTitles = typesOfTransport.map {
            when (it) {
                1 -> R.string.label_bus
                2 -> R.string.label_route_taxi
                3 -> R.string.label_express
                4 -> R.string.label_tram
                else -> R.string.unknown
            }
        }.map { checkNotNull(resources).getString(it) }

        override fun getCount(): Int {
            return typesOfTransport.size
        }

        override fun configureRouter(router: Router, position: Int) {
            if (!router.hasRootController()) {
                router.setRoot(RouterTransaction.with(RouteController(typesOfTransport[position])))
            }
        }

        override fun getPageTitle(position: Int): CharSequence {
            return tabTitles[position]
        }
    }
}
