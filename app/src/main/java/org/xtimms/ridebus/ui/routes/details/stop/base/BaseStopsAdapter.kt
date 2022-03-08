package org.xtimms.ridebus.ui.routes.details.stop.base

import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.IFlexible

abstract class BaseStopsAdapter<T : IFlexible<*>>(
    controller: OnStopClickListener
) : FlexibleAdapter<T>(null, controller, true) {

    /**
     * Listener for browse item clicks.
     */
    val clickListener: OnStopClickListener = controller

    /**
     * Listener which should be called when user clicks the download icons.
     */
    interface OnStopClickListener {
        fun onStopClick(position: Int)
    }
}
