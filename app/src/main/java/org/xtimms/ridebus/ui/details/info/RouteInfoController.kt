package org.xtimms.ridebus.ui.details.info

import android.view.LayoutInflater
import android.view.View
import dev.chrisbanes.insetter.applyInsetter
import org.xtimms.ridebus.R
import org.xtimms.ridebus.data.database.entity.Route
import org.xtimms.ridebus.databinding.RouteInfoControllerBinding
import org.xtimms.ridebus.ui.base.controller.NucleusController
import org.xtimms.ridebus.ui.details.RouteDetailsController

class RouteInfoController :
    NucleusController<RouteInfoControllerBinding, RouteInfoPresenter>() {

    override fun createBinding(inflater: LayoutInflater) = RouteInfoControllerBinding.inflate(inflater)

    override fun createPresenter(): RouteInfoPresenter {
        val ctrl = parentController as RouteDetailsController
        return RouteInfoPresenter(ctrl.route!!)
    }

    override fun onViewCreated(view: View) {
        super.onViewCreated(view)

        binding.scrollView.applyInsetter {
            type(navigationBars = true) {
                padding()
            }
        }
    }

    /**
     * Update view with route information
     *
     * @param route manga object containing information about route.
     */
    fun onNextRoute(route: Route) {
        setRouteInfo(route)
    }

    /**
     * Update the view with route information.
     *
     * @param route route object containing information about route.
     */
    private fun setRouteInfo(route: Route) {
        val view = view ?: return

        binding.number.text = if (route.number.isBlank()) {
            "?"
        } else {
            route.number
        }

        binding.title.text = if (route.title.isBlank()) {
            view.context.getString(R.string.unknown)
        } else {
            route.title
        }

        binding.paymentMethods.subtitle = if (route.title.isBlank()) {
            view.context.getString(R.string.unknown)
        } else {
            route.paymentMethods
        }

        binding.fare.subtitle = if (route.title.isBlank()) {
            view.context.getString(R.string.unknown)
        } else {
            route.fare
        }

        binding.traffic.subtitle = if (route.title.isBlank()) {
            view.context.getString(R.string.unknown)
        } else {
            route.weeklyTraffic
        }

        binding.time.subtitle = if (route.title.isBlank()) {
            view.context.getString(R.string.unknown)
        } else {
            route.workingHours
        }

        binding.following.subtitle = if (route.title.isBlank()) {
            view.context.getString(R.string.unknown)
        } else {
            route.following
        }

        binding.company.subtitle = if (route.title.isBlank()) {
            view.context.getString(R.string.unknown)
        } else {
            route.carrierCompany
        }

        binding.techInfo.subtitle = if (route.title.isBlank()) {
            view.context.getString(R.string.unknown)
        } else {
            route.techInfo
        }
    }
}
