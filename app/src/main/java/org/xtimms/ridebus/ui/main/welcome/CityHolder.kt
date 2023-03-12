package org.xtimms.ridebus.ui.main.welcome

import android.view.View
import eu.davidea.viewholders.FlexibleViewHolder
import org.xtimms.ridebus.databinding.CityItemBinding

class CityHolder(view: View, val adapter: CityAdapter) :
    FlexibleViewHolder(view, adapter) {

    private val binding = CityItemBinding.bind(view)

    init {
        binding.holder.setOnClickListener {
            adapter.itemClickListener.onItemClick(bindingAdapterPosition)
        }
    }

    fun bind(item: CityItem) {
        val city = item.city
        binding.cityTitle.text = city.city
    }
}
