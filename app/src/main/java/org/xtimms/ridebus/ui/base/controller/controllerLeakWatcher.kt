package org.xtimms.ridebus.ui.base.controller

import android.view.View
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.ControllerChangeHandler
import com.bluelinelabs.conductor.ControllerChangeType
import leakcanary.AppWatcher

private class RefWatchingControllerLifecycleListener : Controller.LifecycleListener() {

    private var hasExited = false

    override fun postDestroy(controller: Controller) {
        if (hasExited) {
            controller.expectWeaklyReachable()
        }
    }

    override fun preDestroyView(controller: Controller, view: View) {
        AppWatcher.objectWatcher.expectWeaklyReachable(
            view,
            "A destroyed controller view should have only weak references."
        )
    }

    override fun onChangeEnd(
        controller: Controller,
        changeHandler: ControllerChangeHandler,
        changeType: ControllerChangeType
    ) {
        hasExited = !changeType.isEnter
        if (controller.isDestroyed) {
            controller.expectWeaklyReachable()
        }
    }

    private fun Controller.expectWeaklyReachable() {
        AppWatcher.objectWatcher.expectWeaklyReachable(
            this,
            "A destroyed controller should have only weak references."
        )
    }
}

fun Controller.watchForLeaks() {
    addLifecycleListener(RefWatchingControllerLifecycleListener())
}
