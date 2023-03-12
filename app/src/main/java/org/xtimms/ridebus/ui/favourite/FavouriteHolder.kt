package org.xtimms.ridebus.ui.favourite

import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import eu.davidea.viewholders.FlexibleViewHolder
import org.xtimms.ridebus.R
import org.xtimms.ridebus.databinding.FavouriteItemBinding
import org.xtimms.ridebus.util.view.setVectorCompat

class FavouriteHolder(view: View, val adapter: FavouritesAdapter) :
    FlexibleViewHolder(view, adapter) {

    private val binding = FavouriteItemBinding.bind(view)

    init {
        binding.pin.setOnClickListener {
            adapter.clickListener.onPinClick(bindingAdapterPosition)
        }
    }

    fun bind(item: FavouriteItem) {
        val favourite = item.route

        binding.routeNumber.text = favourite?.number
        binding.routeTitle.text = favourite?.title
        binding.routeDescription.text = favourite?.description

        // TODO better
        when (favourite?.transportId) {
            1 -> binding.circle.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.bus_primaryContainer))
            2 -> binding.circle.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.minibus_primaryContainer))
            3 -> binding.circle.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.express_primaryContainer))
            4 -> binding.circle.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.tram_primaryContainer))
        }

        when (favourite?.transportId) {
            1 -> binding.routeNumber.setTextColor(ContextCompat.getColor(itemView.context, R.color.bus_onPrimaryContainer))
            2 -> binding.routeNumber.setTextColor(ContextCompat.getColor(itemView.context, R.color.minibus_onPrimaryContainer))
            3 -> binding.routeNumber.setTextColor(ContextCompat.getColor(itemView.context, R.color.express_onPrimaryContainer))
            4 -> binding.routeNumber.setTextColor(ContextCompat.getColor(itemView.context, R.color.tram_onPrimaryContainer))
        }

        binding.pin.isVisible = true
        if (item.isPinned) {
            binding.pin.setVectorCompat(R.drawable.ic_push_pin_filled, com.google.android.material.R.attr.colorPrimary)
        } else {
            binding.pin.setVectorCompat(R.drawable.ic_push_pin_outline, com.google.android.material.R.attr.colorOnSurfaceVariant)
        }
    }
}
