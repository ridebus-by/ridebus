package org.xtimms.ridebus.ui.routes.express

import android.view.View
import eu.davidea.viewholders.FlexibleViewHolder
import org.xtimms.ridebus.databinding.RoutesItemBinding

class ExpressHolder(view: View, val adapter: ExpressAdapter) :
    FlexibleViewHolder(view, adapter) {

    private val binding = RoutesItemBinding.bind(view)

    init {
        binding.holder.setOnClickListener {
            adapter.itemClickListener.onItemClick(bindingAdapterPosition)
        }
    }

    fun bind(item: ExpressItem) {
        val route = item.route
        binding.routeNumber.text = route.number
        binding.routeTitle.text = route.title
        binding.routeDescription.text = route.description
    }
}
