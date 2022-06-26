package org.xtimms.ridebus.ui.routes.details.stop

import android.view.View
import com.github.vipulasri.timelineview.TimelineView
import org.xtimms.ridebus.databinding.RouteDetailStopItemBinding
import org.xtimms.ridebus.ui.routes.details.stop.base.BaseStopHolder

class StopOnRouteHolder(
    view: View,
    val adapter: StopsOnRouteAdapter
) : BaseStopHolder(view, adapter) {

    private val binding = RouteDetailStopItemBinding.bind(view)

    fun bind(item: StopOnRouteItem) {
        val stop = item.stop
        binding.timeline.initLine(TimelineView.getTimeLineViewType(bindingAdapterPosition, adapter.itemCount))
        binding.stopTitle.text = stop.name
        binding.stopDescription.text = stop.direction
    }
}
