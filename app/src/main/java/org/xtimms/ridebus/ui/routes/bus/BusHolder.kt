package org.xtimms.ridebus.ui.routes.bus

import android.view.View
import eu.davidea.viewholders.FlexibleViewHolder
import org.xtimms.ridebus.databinding.RoutesItemBinding

class BusHolder(view: View, val adapter: BusAdapter) :
    FlexibleViewHolder(view, adapter) {

    private val binding = RoutesItemBinding.bind(view)

    fun bind(item: BusItem) {
        val route = item.route
        binding.routeNumber.text = route.number
        binding.routeTitle.text = route.title
        binding.routeDescription.text = route.description
    }
}
