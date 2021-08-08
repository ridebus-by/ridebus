package org.xtimms.ridebus.util.view

import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.widget.TooltipCompat
import androidx.recyclerview.widget.RecyclerView

/**
 * Adds a tooltip shown on long press.
 *
 * @param stringRes String resource for tooltip.
 */
inline fun View.setTooltip(@StringRes stringRes: Int) {
    setTooltip(context.getString(stringRes))
}

/**
 * Adds a tooltip shown on long press.
 *
 * @param text Text for tooltip.
 */
inline fun View.setTooltip(text: String) {
    TooltipCompat.setTooltipText(this, text)
}

/**
 * Callback will be run immediately when no animation running
 */
fun RecyclerView.onAnimationsFinished(callback: (RecyclerView) -> Unit) = post(
    object : Runnable {
        override fun run() {
            if (isAnimating) {
                itemAnimator?.isRunning {
                    post(this)
                }
            } else {
                callback(this@onAnimationsFinished)
            }
        }
    }
)