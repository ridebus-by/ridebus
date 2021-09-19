package org.xtimms.ridebus.ui.routes

import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.IFlexible

class RouteAdapter(controller: RouteController) :
    FlexibleAdapter<IFlexible<*>>(null, controller, true) {

    val itemClickListener: OnItemClickListener = controller

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}
