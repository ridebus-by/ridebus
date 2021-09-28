package org.xtimms.ridebus.ui.main

import android.animation.ObjectAnimator
import android.view.View
import android.view.ViewTreeObserver
import androidx.annotation.Keep
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import org.xtimms.ridebus.data.preference.PreferencesHelper
import uy.kohesive.injekt.injectLazy

class ViewHeightAnimator(val view: View) {

    private val preferences: PreferencesHelper by injectLazy()

    /**
     * The default height of the view. It's unknown until the view is layout.
     */
    private var height = 0

    /**
     * Whether the last state of the view is shown or hidden.
     */
    private var isLastStateShown = true

    /**
     * Animation used to expand the view.
     */
    private val expandAnimation by lazy {
        ObjectAnimator.ofInt(this, "height", height).apply {
            duration = if (preferences.reducedMotion()) {
                0L
            } else {
                300L
            }
            interpolator = LinearOutSlowInInterpolator()
        }
    }

    /**
     * Animation used to collapse the view.
     */
    private val collapseAnimation by lazy {
        ObjectAnimator.ofInt(this, "height", height).apply {
            duration = if (preferences.reducedMotion()) {
                0L
            } else {
                250L
            }
            interpolator = LinearOutSlowInInterpolator()
        }
    }

    init {
        view.viewTreeObserver.addOnGlobalLayoutListener(
            object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    if (view.height > 0) {
                        view.viewTreeObserver.removeOnGlobalLayoutListener(this)

                        // Save the tabs default height.
                        height = view.height

                        // Now that we know the height, set the initial height.
                        if (isLastStateShown) {
                            setHeight(height)
                        } else {
                            setHeight(0)
                        }
                    }
                }
            }
        )
    }

    /**
     * Sets the height of the tab layout.
     *
     * @param newHeight The new height of the tab layout.
     */
    @Keep
    fun setHeight(newHeight: Int) {
        view.layoutParams.height = newHeight
        view.requestLayout()
    }

    /**
     * Returns the height of the tab layout. This method is also called from the animator through
     * reflection.
     */
    fun getHeight(): Int {
        return view.layoutParams.height
    }

    /**
     * Expands the tab layout with an animation.
     */
    fun expand() {
        if (isMeasured) {
            if (getHeight() != height) {
                expandAnimation.setIntValues(height)
                expandAnimation.start()
            } else {
                expandAnimation.cancel()
            }
        }
        isLastStateShown = true
    }

    /**
     * Collapse the tab layout with an animation.
     */
    fun collapse() {
        if (isMeasured) {
            if (getHeight() != 0) {
                collapseAnimation.setIntValues(0)
                collapseAnimation.start()
            } else {
                collapseAnimation.cancel()
            }
        }
        isLastStateShown = false
    }

    /**
     * Returns whether the tab layout has a known height.
     */
    private val isMeasured: Boolean
        get() = height > 0
}
