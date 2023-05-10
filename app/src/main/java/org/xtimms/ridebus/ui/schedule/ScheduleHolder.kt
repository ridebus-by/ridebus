package org.xtimms.ridebus.ui.schedule

import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import androidx.core.text.buildSpannedString
import eu.davidea.viewholders.FlexibleViewHolder
import org.xtimms.ridebus.databinding.ScheduleItemBinding
import org.xtimms.ridebus.util.system.getThemeColor
import com.google.android.material.R as materialR

class ScheduleHolder(view: View, val adapter: ScheduleAdapter) :
    FlexibleViewHolder(view, adapter) {

    private val binding = ScheduleItemBinding.bind(view)
    private val highlightColor = view.context.getThemeColor(materialR.attr.colorPrimary)
    private val bgHighlightColor =
        view.context.getThemeColor(materialR.attr.colorSecondaryContainer)

    fun bind(item: ScheduleRow) {
        binding.textHour.text = item.hour.toString()
        binding.textHour.setBackgroundColor(
            if (item.isCurrentHour()) bgHighlightColor else Color.TRANSPARENT
        )
        val closestMinute = item.getClosestMinute()
        binding.textMinutes.text = buildSpannedString {
            var isFirst = true
            for (minute in item.minutes) {
                if (isFirst) {
                    isFirst = false
                } else {
                    append("\t\t\t")
                }
                append("%02d".format(minute))
                if (closestMinute == minute) {
                    setSpan(
                        ForegroundColorSpan(highlightColor),
                        length - 2,
                        length,
                        Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                    )
                    setSpan(
                        StyleSpan(Typeface.BOLD),
                        length - 2,
                        length,
                        Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                    )
                }
            }
        }
    }
}
