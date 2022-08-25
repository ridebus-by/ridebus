package org.xtimms.ridebus.widget

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import org.xtimms.ridebus.R
import org.xtimms.ridebus.databinding.StopTimeViewBinding
import org.xtimms.ridebus.util.Times

class ArrivalTimeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private val binding = StopTimeViewBinding.inflate(LayoutInflater.from(context), this)
    private val timeReceiver = TimeReceiver()
    private var cachedTime: Times.Time? = null

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
            binding.remainingTime.text = null
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
        }
        cachedTime = time
    }

    private fun update() {
        val now = Times.Time.now()
        if (times == null) {
            bind(null, now)
            return
        }
        val closestTime = times?.closest(now)
        if (closestTime != cachedTime) {
            bind(closestTime, now)
        }
    }

    private inner class TimeReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            update()
        }
    }
}
