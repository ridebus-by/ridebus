package org.xtimms.ridebus.ui.details.route.stops

import eu.davidea.flexibleadapter.FlexibleAdapter

class RouteStopsAdapter(
    controller: RouteStopsController
) : FlexibleAdapter<RouteStopsItem>(null, controller, true) {

    val itemClickListener: OnItemClickListener = controller

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}
