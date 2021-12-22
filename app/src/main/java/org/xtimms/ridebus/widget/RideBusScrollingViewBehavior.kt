package org.xtimms.ridebus.widget

import com.google.android.material.appbar.AppBarLayout

/**
 * [AppBarLayout.ScrollingViewBehavior] that lets the app bar overlaps the scrolling child.
 */
class RideBusScrollingViewBehavior : AppBarLayout.ScrollingViewBehavior() {

    var shouldHeaderOverlap = false

    override fun shouldHeaderOverlapScrollingChild(): Boolean {
        return shouldHeaderOverlap
    }
}
