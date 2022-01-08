package org.xtimms.ridebus.ui.routes

import android.view.View
import eu.davidea.viewholders.FlexibleViewHolder
import org.xtimms.ridebus.data.preference.PreferencesHelper
import org.xtimms.ridebus.databinding.RoutesItemBinding
import org.xtimms.ridebus.util.lang.transliterate
import uy.kohesive.injekt.injectLazy

class RouteHolder(view: View, val adapter: RouteAdapter) :
    FlexibleViewHolder(view, adapter) {

    private val preferences: PreferencesHelper by injectLazy()

    private val binding = RoutesItemBinding.bind(view)

    init {
        binding.holder.setOnClickListener {
            adapter.itemClickListener.onItemClick(bindingAdapterPosition)
        }
    }

    fun bind(item: RouteItem) {
        val route = item.route
        binding.routeNumber.text = route.number
        if (preferences.transliterate().get()) {
            binding.routeTitle.text = route.title.transliterate()
            binding.routeDescription.text = route.description.transliterate()
        } else {
            binding.routeTitle.text = route.title
            binding.routeDescription.text = route.description
        }
    }
}
