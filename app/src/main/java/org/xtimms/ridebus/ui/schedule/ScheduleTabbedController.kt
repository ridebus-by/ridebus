package org.xtimms.ridebus.ui.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.os.bundleOf
import com.bluelinelabs.conductor.*
import com.bluelinelabs.conductor.viewpager.RouterPagerAdapter
import com.google.android.material.tabs.TabLayout
import org.xtimms.ridebus.R
import org.xtimms.ridebus.data.database.RideBusDatabase
import org.xtimms.ridebus.data.database.entity.Route
import org.xtimms.ridebus.data.database.entity.Stop
import org.xtimms.ridebus.databinding.ScheduleTabbedControllerBinding
import org.xtimms.ridebus.ui.base.controller.NoAppBarElevationController
import org.xtimms.ridebus.ui.base.controller.RxController
import org.xtimms.ridebus.ui.base.controller.TabbedController
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class ScheduleTabbedController :
    RxController<ScheduleTabbedControllerBinding>,
    NoAppBarElevationController,
    TabbedController {

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
            binding.tabs.apply {
                setupWithViewPager(binding.pager)
            }
        }
    }

    override fun configureTabs(tabs: TabLayout): Boolean {
        with(tabs) {
            tabGravity = TabLayout.GRAVITY_FILL
            tabMode = TabLayout.MODE_FIXED
        }
        return false
    }

    private inner class ScheduleRouterPagerAdapter : RouterPagerAdapter(this@ScheduleTabbedController) {

        private val tabTitles = listOf(
            R.string.label_working_days,
            R.string.label_weekends
        )
            .map { resources!!.getString(it) }

        override fun getCount(): Int {
            return tabTitles.size
        }

        override fun configureRouter(router: Router, position: Int) {
            if (!router.hasRootController()) {
                val controller: Controller = when (position) {
                    WORKING_DAYS_CONTROLLER -> ScheduleController(WORKING_DAY, route, stop)
                    WEEKENDS_CONTROLLER -> ScheduleController(WEEKEND, route, stop)
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
        const val WORKING_DAYS_CONTROLLER = 0
        const val WEEKENDS_CONTROLLER = 1
        const val ROUTE_EXTRA = "route"
        const val STOP_EXTRA = "stop"
        const val WORKING_DAY = 1
        const val WEEKEND = 2
    }
}
