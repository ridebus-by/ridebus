package org.xtimms.ridebus.ui.schedule

import android.view.View
import eu.davidea.viewholders.FlexibleViewHolder
import org.xtimms.ridebus.databinding.ScheduleItemLegacyBinding

class ScheduleHolder(view: View, val adapter: ScheduleAdapter) :
    FlexibleViewHolder(view, adapter) {

    private val binding = ScheduleItemLegacyBinding.bind(view)

    fun bind(item: ScheduleItem) {
        val schedule = item.schedule
        binding.textTimeExact.text = schedule.arrivalTime
    }
}
