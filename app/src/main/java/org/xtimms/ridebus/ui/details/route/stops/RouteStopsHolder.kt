package org.xtimms.ridebus.ui.details.route.stops

import android.view.View
import com.github.vipulasri.timelineview.TimelineView
import eu.davidea.viewholders.FlexibleViewHolder
import org.xtimms.ridebus.databinding.RouteStopItemBinding

class RouteStopsHolder(view: View, val adapter: RouteStopsAdapter) :
    FlexibleViewHolder(view, adapter) {

    private val binding = RouteStopItemBinding.bind(view)

    init {
        binding.holder.setOnClickListener {
            adapter.itemClickListener.onItemClick(bindingAdapterPosition)
        }
    }

    fun bind(item: RouteStopsItem) {
        val stop = item.stop
        binding.timeline.initLine(TimelineView.getTimeLineViewType(bindingAdapterPosition, adapter.itemCount))
        binding.stopTitle.text = stop.name
    }
}
