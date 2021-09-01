package org.xtimms.ridebus.ui.base.controller

import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.RouterTransaction

fun Controller.withFadeTransaction(): RouterTransaction {
    return RouterTransaction.with(this)
        .pushChangeHandler(OneWayFadeChangeHandler())
        .popChangeHandler(OneWayFadeChangeHandler())
}
