package org.xtimms.ridebus.ui.routes

import android.view.LayoutInflater
import android.view.View
import com.bluelinelabs.conductor.*
import com.bluelinelabs.conductor.support.RouterPagerAdapter
import com.google.android.material.tabs.TabLayout
import org.xtimms.ridebus.R
import org.xtimms.ridebus.databinding.PagerControllerBinding
import org.xtimms.ridebus.ui.base.controller.RootController
import org.xtimms.ridebus.ui.base.controller.RxController
import org.xtimms.ridebus.ui.base.controller.TabbedController
import org.xtimms.ridebus.ui.main.MainActivity

class RoutesController:
    RxController<PagerControllerBinding>(),
    RootController,
    TabbedController {

    private var adapter: RoutesAdapter? = null

    override fun getTitle(): String? {
        return resources!!.getString(R.string.title_routes)
    }

    override fun createBinding(inflater: LayoutInflater) = PagerControllerBinding.inflate(inflater)

    override fun onViewCreated(view: View) {
        super.onViewCreated(view)

        adapter = RoutesAdapter()
        binding.pager.adapter = adapter
    }

    override fun onDestroyView(view: View) {
        super.onDestroyView(view)
        adapter = null
    }

    override fun onChangeStarted(handler: ControllerChangeHandler, type: ControllerChangeType) {
        super.onChangeStarted(handler, type)
        if (type.isEnter) {
            (activity as? MainActivity)?.binding?.tabs?.apply {
                setupWithViewPager(binding.pager)
            }
        }
    }

    override fun configureTabs(tabs: TabLayout) {
        with(tabs) {
            tabGravity = TabLayout.GRAVITY_FILL
            tabMode = TabLayout.MODE_FIXED
        }
    }

    private inner class RoutesAdapter : RouterPagerAdapter(this@RoutesController) {

        private val tabTitles = listOf(
            R.string.label_bus,
            R.string.label_route_taxi,
            R.string.label_express
        )
            .map { resources!!.getString(it) }

        override fun getCount(): Int {
            return tabTitles.size
        }

        override fun configureRouter(router: Router, position: Int) {
            /*if (!router.hasRootController()) {
                val controller: Controller = when (position) {
                    BUS_CONTROLLER -> BusController()
                    ROUTE_TAXI_CONTROLLER -> RouteTaxiController()
                    EXPRESS_CONTROLLER -> ExpressController()
                    else -> error("Wrong position $position")
                }
                router.setRoot(RouterTransaction.with(controller))
            }*/
        }

        override fun getPageTitle(position: Int): CharSequence {
            return tabTitles[position]
        }
    }

    companion object {
        const val BUS_CONTROLLER = 0
        const val ROUTE_TAXI_CONTROLLER = 1
        const val EXPRESS_CONTROLLER = 2
    }

}