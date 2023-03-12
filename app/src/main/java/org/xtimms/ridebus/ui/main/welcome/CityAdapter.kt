package org.xtimms.ridebus.ui.main.welcome

import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.IFlexible

class CityAdapter(controller: WelcomeDialogController) :
    FlexibleAdapter<IFlexible<*>>(null, controller, true) {

    val itemClickListener: OnItemClickListener = controller

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}
