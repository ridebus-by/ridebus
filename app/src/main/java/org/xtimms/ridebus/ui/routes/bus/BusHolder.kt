package org.xtimms.ridebus.ui.routes.bus

import android.view.View
import eu.davidea.viewholders.FlexibleViewHolder
import org.xtimms.ridebus.databinding.RoutesItemBinding

class BusHolder(view: View, val adapter: BusAdapter) :
    FlexibleViewHolder(view, adapter) {

    private val binding = RoutesItemBinding.bind(view)

    fun bind(item: BusItem) {
        val bus = item.route

        binding.routeTitle.text = bus.title
        binding.routeNumber.text = bus.number
    }
}
