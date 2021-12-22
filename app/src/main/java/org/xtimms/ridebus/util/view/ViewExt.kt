package org.xtimms.ridebus.util.view

import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.appcompat.widget.TooltipCompat
import androidx.core.view.children
import androidx.core.view.descendants
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

/**
 * Returns this ViewGroup's first child of specified class
 */
inline fun <reified T> ViewGroup.findChild(): T? {
    return children.find { it is T } as? T
}

/**
 * Returns this ViewGroup's first descendant of specified class
 */
inline fun <reified T> ViewGroup.findDescendant(): T? {
    return descendants.find { it is T } as? T
}
