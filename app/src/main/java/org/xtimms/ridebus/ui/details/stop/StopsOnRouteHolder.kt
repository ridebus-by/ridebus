package org.xtimms.ridebus.ui.details.stop

import android.view.View
import androidx.core.content.ContextCompat
import eu.davidea.viewholders.FlexibleViewHolder
import org.xtimms.ridebus.R
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
        when (route.transportId) {
            1 -> binding.decorView.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.bus))
            2 -> binding.decorView.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.minibus))
            3 -> binding.decorView.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.express))
        }
    }
}
