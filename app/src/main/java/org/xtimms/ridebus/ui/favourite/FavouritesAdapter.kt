package org.xtimms.ridebus.ui.favourite

import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.IFlexible

class FavouritesAdapter(controller: FavouritesController) :
    FlexibleAdapter<IFlexible<*>>(null, controller, true) {

    init {
        setDisplayHeadersAtStartUp(true)
    }

    val clickListener: OnFavouriteItemClickListener = controller

    interface OnFavouriteItemClickListener {
        fun onItemClick(position: Int)
        fun onPinClick(position: Int)
    }
}
