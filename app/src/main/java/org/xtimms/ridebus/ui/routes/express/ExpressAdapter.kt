package org.xtimms.ridebus.ui.routes.express

import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.IFlexible

class ExpressAdapter(controller: ExpressController) :
    FlexibleAdapter<IFlexible<*>>(null, controller, true) {

    val itemClickListener: OnItemClickListener = controller

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}
