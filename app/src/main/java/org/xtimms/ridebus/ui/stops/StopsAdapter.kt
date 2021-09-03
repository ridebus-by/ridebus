package org.xtimms.ridebus.ui.stops

import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.IFlexible

//
// Created by Xtimms on 01.09.2021.
//
class StopsAdapter(controller: StopsController) :
    FlexibleAdapter<IFlexible<*>>(null, controller, true) {

    val itemClickListener: OnItemClickListener = controller

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}
