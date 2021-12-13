package org.xtimms.ridebus.ui.details.route

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import com.bluelinelabs.conductor.*
import com.bluelinelabs.conductor.viewpager.RouterPagerAdapter
import com.google.android.material.tabs.TabLayout
import logcat.LogPriority
import org.xtimms.ridebus.BuildConfig
import org.xtimms.ridebus.R
import org.xtimms.ridebus.data.database.RideBusDatabase
import org.xtimms.ridebus.data.database.entity.Route
import org.xtimms.ridebus.databinding.RoutesDetailControllerBinding
import org.xtimms.ridebus.ui.base.controller.*
import org.xtimms.ridebus.ui.details.route.info.RouteInfoController
import org.xtimms.ridebus.ui.details.route.stops.RouteStopsController
import org.xtimms.ridebus.ui.main.MainActivity
import org.xtimms.ridebus.util.system.logcat
import org.xtimms.ridebus.util.system.toast
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class RouteDetailsController :
    RxController<RoutesDetailControllerBinding>,
    TabbedController {

    constructor(route: Route?) : super(
        Bundle().apply {
            putInt(ROUTE_EXTRA, route?.routeId ?: 0)
        }
    ) {
        this.route = route
    }

    constructor(routeId: Int) : this(Injekt.get<RideBusDatabase>().routeDao().getRoute(routeId).firstOrNull())

    @Suppress("unused")
    constructor(bundle: Bundle) : this(bundle.getInt(ROUTE_EXTRA))

    var route: Route? = null
        private set

    private var adapter: RouteDetailsAdapter? = null

    init {
        setHasOptionsMenu(true)
    }

    override fun getTitle(): String {
        return "${resources!!.getString(R.string.label_route)} №${route?.number}"
    }

    override fun createBinding(inflater: LayoutInflater) = RoutesDetailControllerBinding.inflate(inflater)

    override fun onViewCreated(view: View) {
        super.onViewCreated(view)

        if (route == null) return

        adapter = RouteDetailsAdapter()
        binding.pager.offscreenPageLimit = 2
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.route_info, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_report -> report()
        }
        return super.onOptionsItemSelected(item)
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

    private inner class RouteDetailsAdapter : RouterPagerAdapter(this@RouteDetailsController) {

        private val tabTitles = listOf(
            R.string.label_info,
            R.string.title_stops
        )
            .map { resources!!.getString(it) }

        override fun getCount(): Int {
            return tabTitles.size
        }

        override fun configureRouter(router: Router, position: Int) {
            if (!router.hasRootController()) {
                val controller: Controller = when (position) {
                    INFO_CONTROLLER -> RouteInfoController()
                    STOPS_CONTROLLER -> RouteStopsController()
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
        const val ROUTE_EXTRA = "route"

        const val INFO_CONTROLLER = 0
        const val STOPS_CONTROLLER = 1
    }
}
