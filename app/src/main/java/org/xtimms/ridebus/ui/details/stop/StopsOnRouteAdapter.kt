package org.xtimms.ridebus.ui.details.stop

import eu.davidea.flexibleadapter.FlexibleAdapter

class StopsOnRouteAdapter(
    controller: StopsOnRouteController
) : FlexibleAdapter<StopsOnRouteItem>(null, controller, true) {

    val itemClickListener: OnItemClickListener = controller

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}
