package org.xtimms.ridebus.ui.base.controller

import androidx.core.net.toUri
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.SimpleSwapChangeHandler
import org.xtimms.ridebus.data.preference.PreferencesHelper
import org.xtimms.ridebus.ui.main.MainActivity
import org.xtimms.ridebus.util.system.openInBrowser
import uy.kohesive.injekt.injectLazy

fun Controller.withFadeTransaction(): RouterTransaction {
    val preferences: PreferencesHelper by injectLazy()
    return if (preferences.reducedMotion()) {
        RouterTransaction.with(this)
            .pushChangeHandler(SimpleSwapChangeHandler())
            .popChangeHandler(SimpleSwapChangeHandler())
    } else {
        RouterTransaction.with(this)
            .pushChangeHandler(OneWayFadeChangeHandler())
            .popChangeHandler(OneWayFadeChangeHandler())
    }
}

/**
 * Returns [MainActivity]'s app bar height
 */
fun Controller.getMainAppBarHeight(): Int {
    return (activity as? MainActivity)?.binding?.appbar?.measuredHeight ?: 0
}

fun Controller.openInBrowser(url: String) {
    activity?.openInBrowser(url.toUri())
}
