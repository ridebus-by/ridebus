package org.xtimms.ridebus.ui.schedule

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import org.xtimms.ridebus.R
import org.xtimms.ridebus.data.database.entity.Schedule

class ScheduleItem(val schedule: Schedule) : AbstractFlexibleItem<ScheduleHolder>() {

    override fun equals(other: Any?): Boolean {
        if (other is ScheduleItem) {
            return schedule.id == other.schedule.id
        }
        return false
    }

    override fun hashCode(): Int {
        return schedule.id.hashCode()
    }

    override fun getLayoutRes(): Int {
        return R.layout.schedule_item_legacy
    }

    override fun createViewHolder(
        view: View,
        adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>
    ): ScheduleHolder {
        return ScheduleHolder(view, adapter as ScheduleAdapter)
    }

    override fun bindViewHolder(
        adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>,
        holder: ScheduleHolder,
        position: Int,
        payloads: List<Any?>?
    ) {
        holder.bind(this)
    }
}
