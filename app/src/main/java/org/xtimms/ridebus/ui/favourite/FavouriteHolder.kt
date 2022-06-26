package org.xtimms.ridebus.ui.favourite

import android.view.View
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

        binding.routeNumber.text = favourite.number
        binding.routeTitle.text = favourite.title
        binding.routeDescription.text = favourite.description

        binding.pin.isVisible = true
        if (item.isPinned) {
            binding.pin.setVectorCompat(R.drawable.ic_push_pin_filled, androidx.appcompat.R.attr.colorAccent)
        } else {
            binding.pin.setVectorCompat(R.drawable.ic_push_pin_outline, android.R.attr.textColorHint)
        }
    }
}
