package org.xtimms.ridebus.ui.details.stop

import android.view.View
import eu.davidea.viewholders.FlexibleViewHolder
import org.xtimms.ridebus.databinding.StopsRouteItemBinding

class StopsOnRouteHolder(view: View, val adapter: StopsOnRouteAdapter) :
    FlexibleViewHolder(view, adapter) {

    private val binding = StopsRouteItemBinding.bind(view)

    init {
        binding.holder.setOnClickListener {
            adapter.itemClickListener.onItemClick(bindingAdapterPosition)
        }
    }

    fun bind(item: StopsOnRouteItem) {
        val route = item.route
        binding.routeTitle.text = route.title
        binding.routeNumber.text = route.number
    }
}
