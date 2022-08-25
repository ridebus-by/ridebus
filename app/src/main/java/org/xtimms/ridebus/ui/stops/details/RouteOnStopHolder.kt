package org.xtimms.ridebus.ui.stops.details

import android.view.View
import androidx.core.content.ContextCompat
import eu.davidea.viewholders.FlexibleViewHolder
import org.xtimms.ridebus.R
import org.xtimms.ridebus.databinding.StopsRouteItemBinding

class RouteOnStopHolder(view: View, val adapter: RoutesOnStopAdapter) :
    FlexibleViewHolder(view, adapter) {

    private val binding = StopsRouteItemBinding.bind(view)

    init {
        binding.holder.setOnClickListener {
            adapter.itemClickListener.onItemClick(bindingAdapterPosition)
        }
    }

    fun bind(item: RoutesOnStopItem) {
        val route = item.route
        binding.routeTitle.text = route.title
        binding.routeNumber.text = route.number
        binding.timeView.times = item.times
        when (route.transportId) {
            1 -> binding.decorView.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.bus_primary))
            2 -> binding.decorView.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.minibus_primary))
            3 -> binding.decorView.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.express_primary))
        }
    }
}
