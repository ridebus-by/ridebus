package org.xtimms.ridebus.ui.routes.taxi

import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.IFlexible

class TaxiAdapter(controller: TaxiController) :
    FlexibleAdapter<IFlexible<*>>(null, controller, true) {

    val itemClickListener: OnItemClickListener = controller

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}
