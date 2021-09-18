package org.xtimms.ridebus.ui.routes.taxi

import android.view.View
import eu.davidea.viewholders.FlexibleViewHolder
import org.xtimms.ridebus.databinding.RoutesItemBinding

class TaxiHolder(view: View, val adapter: TaxiAdapter) :
    FlexibleViewHolder(view, adapter) {

    private val binding = RoutesItemBinding.bind(view)

    init {
        binding.holder.setOnClickListener {
            adapter.itemClickListener.onItemClick(bindingAdapterPosition)
        }
    }

    fun bind(item: TaxiItem) {
        val route = item.route
        binding.routeNumber.text = route.number
        binding.routeTitle.text = route.title
        binding.routeDescription.text = route.description
    }
}
