package org.xtimms.ridebus.ui.stops.details

import eu.davidea.flexibleadapter.FlexibleAdapter

class RoutesOnStopAdapter(
    controller: RoutesOnStopController
) : FlexibleAdapter<RoutesOnStopItem>(null, controller, true) {

    val itemClickListener: OnItemClickListener = controller

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}
