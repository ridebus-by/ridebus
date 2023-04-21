package org.xtimms.ridebus.ui.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.os.bundleOf
import com.bluelinelabs.conductor.*
import com.bluelinelabs.conductor.viewpager.RouterPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.jakewharton.rxrelay.BehaviorRelay
import org.xtimms.ridebus.R
import org.xtimms.ridebus.data.database.RideBusDatabase
import org.xtimms.ridebus.data.database.entity.Route
import org.xtimms.ridebus.data.database.entity.Stop
import org.xtimms.ridebus.databinding.ScheduleTabbedControllerBinding
import org.xtimms.ridebus.ui.base.controller.BaseController
import org.xtimms.ridebus.ui.base.controller.NoAppBarElevationController
import org.xtimms.ridebus.ui.base.controller.TabbedController
import rx.Subscription
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get
import uy.kohesive.injekt.injectLazy
import java.util.*

class ScheduleTabbedController :
    BaseController<ScheduleTabbedControllerBinding>,
    NoAppBarElevationController,
    TabbedController {

    private val db: RideBusDatabase by injectLazy()

    private var tabsVisibilityRelay: BehaviorRelay<Boolean> = BehaviorRelay.create(false)
    private var tabsVisibilitySubscription: Subscription? = null

    private val typesOfDay: List<Int>
        get() = db.scheduleDao().getTypesOfDay(route?.routeId ?: 0)

    constructor(route: Route?, stop: Stop?) : super(
        bundleOf(
            ROUTE_EXTRA to (route?.routeId ?: 0),
            STOP_EXTRA to (stop?.stopId ?: 0)
        )
    ) {
        this.route = route
        this.stop = stop
    }

    constructor(routeId: Int, stopId: Int) : this(
        Injekt.get<RideBusDatabase>().routeDao().getRoute(routeId).firstOrNull(),
        Injekt.get<RideBusDatabase>().stopDao().getStop(stopId).firstOrNull()
    )

    @Suppress("unused")
    constructor(bundle: Bundle) : this(
        bundle.getInt(ROUTE_EXTRA),
        bundle.getInt(STOP_EXTRA)
    )

    var route: Route? = null
        private set

    var stop: Stop? = null
        private set

    private var adapter: ScheduleRouterPagerAdapter? = null

    override fun getTitle(): String? {
        return resources?.getString(R.string.schedule)
    }

    override fun createBinding(inflater: LayoutInflater) = ScheduleTabbedControllerBinding.inflate(inflater)

    override fun onViewCreated(view: View) {
        super.onViewCreated(view)

        adapter = ScheduleRouterPagerAdapter()
        binding.pager.adapter = adapter

        binding.number.text = route?.number ?: resources?.getString(R.string.unknown)
        binding.routeTitle.text = route?.title ?: resources?.getString(R.string.unknown)

        binding.stopTitle.text = stop?.name ?: resources?.getString(R.string.unknown)
        binding.stopDescription.text = stop?.direction ?: resources?.getString(R.string.unknown)
    }

    override fun onDestroyView(view: View) {
        super.onDestroyView(view)
        adapter = null
    }

    override fun onChangeStarted(handler: ControllerChangeHandler, type: ControllerChangeType) {
        super.onChangeStarted(handler, type)
        if (type.isEnter) {
            binding.tabLayout.apply {
                setupWithViewPager(binding.pager)
            }
        }
    }

    override fun configureTabs(tabs: TabLayout): Boolean {
        with(tabs) {
            tabGravity = TabLayout.GRAVITY_FILL
            tabMode = TabLayout.MODE_SCROLLABLE
        }
        return false
    }

    private inner class ScheduleRouterPagerAdapter : RouterPagerAdapter(this@ScheduleTabbedController) {

        private val tabTitles = typesOfDay.map {
            when (it) {
                1 -> R.string.label_working_days
                2 -> R.string.label_weekends
                5 -> R.string.label_friday
                6 -> R.string.label_monday_thursday
                7 -> R.string.label_everyday
                else -> R.string.unknown
            }
        }.map { resources!!.getString(it) }

        override fun getCount(): Int {
            return typesOfDay.size
        }

        override fun configureRouter(router: Router, position: Int) {
            if (!router.hasRootController()) {
                // меняем таб в зависимости от типа дня
                // binding.pager.currentItem = typesOfDay[position] - 1
                router.setRoot(RouterTransaction.with(ScheduleController(typesOfDay[position], route, stop)))
            }
        }

        override fun getPageTitle(position: Int): CharSequence {
            return tabTitles[position]
        }
    }

    companion object {
        const val ROUTE_EXTRA = "route"
        const val STOP_EXTRA = "stop"
    }
}
