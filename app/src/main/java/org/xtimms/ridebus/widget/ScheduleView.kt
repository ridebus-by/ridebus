package org.xtimms.ridebus.widget

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.CharacterStyle
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import org.xtimms.ridebus.data.database.RideBusDatabase
import org.xtimms.ridebus.databinding.ScheduleViewBinding
import org.xtimms.ridebus.util.Times
import org.xtimms.ridebus.util.system.getResourceColor
import uy.kohesive.injekt.injectLazy

class ScheduleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private val database: RideBusDatabase by injectLazy()
    private val binding = ScheduleViewBinding.inflate(LayoutInflater.from(context), this)
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
        binding.hour.text = time?.hours.toString()
    }

    private fun getMinutesColor(closestMinutes: Spanned): Spannable {
        val spannable: Spannable = SpannableString(closestMinutes)
        val fgSpan = ForegroundColorSpan(context.getResourceColor(com.google.android.material.R.attr.colorPrimary))
        spannable.setSpan(CharacterStyle.wrap(fgSpan), 0, 2, 0)
        return spannable
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
