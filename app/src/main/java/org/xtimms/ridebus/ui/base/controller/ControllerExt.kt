package org.xtimms.ridebus.ui.base.controller

import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler

fun Controller.withFadeTransaction(): RouterTransaction {
    return RouterTransaction.with(this)
        .pushChangeHandler(FadeChangeHandler())
        .popChangeHandler(FadeChangeHandler())
}
