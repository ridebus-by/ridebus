package org.xtimms.ridebus.ui.schedule

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import org.xtimms.ridebus.R
import java.util.Calendar
import java.util.NavigableSet

class ScheduleRow(
    val hour: Int,
    val minutes: NavigableSet<Int>,
    private val currentTime: Long
) : AbstractFlexibleItem<ScheduleHolder>() {

    private val currentHour: Int
    private val currentMinute: Int

    init {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = currentTime
        currentHour = calendar[Calendar.HOUR_OF_DAY]
        currentMinute = calendar[Calendar.MINUTE]
    }

    fun isCurrentHour() = hour == currentHour

    fun getClosestMinute(): Int? = if (isCurrentHour()) {
        minutes.ceiling(currentMinute)
    } else {
        null
    }

    override fun getLayoutRes(): Int = R.layout.schedule_item

    override fun createViewHolder(
        view: View,
        adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>?
    ): ScheduleHolder = ScheduleHolder(view, adapter as ScheduleAdapter)

    override fun bindViewHolder(
        adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>,
        holder: ScheduleHolder,
        position: Int,
        payloads: MutableList<Any>?
    ) = holder.bind(this)

    override fun toString(): String {
        return minutes.joinToString { minute ->
            "%02d:%02d".format(hour, minute)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ScheduleRow

        if (hour != other.hour) return false
        if (minutes != other.minutes) return false
        return currentTime == other.currentTime
    }

    override fun hashCode(): Int {
        var result = hour
        result = 31 * result + minutes.hashCode()
        result = 31 * result + currentTime.hashCode()
        return result
    }
}
