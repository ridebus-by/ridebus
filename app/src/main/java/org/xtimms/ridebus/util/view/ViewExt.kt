package org.xtimms.ridebus.util.view

import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.appcompat.widget.TooltipCompat
import androidx.core.view.children
import androidx.core.view.descendants
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import org.xtimms.ridebus.util.system.inputMethodManager

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

fun View.hideKeyboard() {
    context.inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
}

/**
 * Shows a snackbar in this view.
 *
 * @param message the message to show.
 * @param length the duration of the snack.
 * @param f a function to execute in the snack, allowing for example to define a custom action.
 */
inline fun View.snack(
    message: String,
    length: Int = Snackbar.LENGTH_SHORT,
    f: Snackbar.() -> Unit = {}
): Snackbar {
    val snack = Snackbar.make(this, message, length)
    snack.f()
    snack.show()
    return snack
}
