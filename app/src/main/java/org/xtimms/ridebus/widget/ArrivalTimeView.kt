package org.xtimms.ridebus.widget

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import org.xtimms.ridebus.R
import org.xtimms.ridebus.databinding.ArrivalTimeViewBinding
import org.xtimms.ridebus.util.Times
import org.xtimms.ridebus.util.system.getResourceColor
import com.google.android.material.R as materialR

class ArrivalTimeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private val binding = ArrivalTimeViewBinding.inflate(LayoutInflater.from(context), this)
    private val timeReceiver = TimeReceiver()

    var times: Times? = null
        set(value) {
            field = value
            update()
        }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        context.registerReceiver(timeReceiver, IntentFilter(Intent.ACTION_TIME_TICK))
        update()
    }

    override fun onDetachedFromWindow() {
        context.unregisterReceiver(timeReceiver)
        super.onDetachedFromWindow()
    }

    private fun bind(time: Times.Time?, now: Times.Time) {
        binding.closestTime.text = time?.toString()
        if (time == null) {
            binding.remainingTime.text = "-"
            binding.closestTime.text = "-"
        } else {
            val relative = time - now
            binding.remainingTime.text = when {
                relative.hours == 0 && relative.minutes == 0 -> context.getString(R.string.token_time_now)
                relative.hours == 0 -> context.getString(
                    R.string.reltime_minutes,
                    relative.minutes
                )
                relative.minutes == 0 -> context.getString(
                    R.string.reltime_hours,
                    relative.hours
                )
                else -> context.getString(
                    R.string.reltime_hours_minutes,
                    relative.hours,
                    relative.minutes
                )
            }
            if (relative.hours == 0 && relative.minutes == 0) {
                binding.remainingTime.apply {
                    setTextColor(context.getResourceColor(materialR.attr.colorPrimary))
                    setTypeface(binding.remainingTime.typeface, Typeface.BOLD)
                }
                binding.closestTime.setTypeface(binding.closestTime.typeface, Typeface.BOLD)
            } else {
                binding.remainingTime.apply {
                    setTextColor(context.getResourceColor(android.R.attr.textColorPrimary))
                    typeface = Typeface.DEFAULT
                }
                binding.closestTime.typeface = Typeface.DEFAULT
            }
        }
    }

    private fun update() {
        val now = Times.Time.now()
        if (times == null) {
            bind(null, now)
            return
        }
        val closestTime = times?.closest(now)
        bind(closestTime, now)
    }

    private inner class TimeReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            update()
        }
    }
}
