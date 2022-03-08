package org.xtimms.ridebus.ui.base.controller

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

interface FabController {

    fun configureFab(fab: ExtendedFloatingActionButton) {}

    fun cleanupFab(fab: ExtendedFloatingActionButton) {}
}
