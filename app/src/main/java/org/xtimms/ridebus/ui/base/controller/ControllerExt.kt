package org.xtimms.ridebus.ui.base.controller

import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.SimpleSwapChangeHandler
import org.xtimms.ridebus.data.preference.PreferencesHelper
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
