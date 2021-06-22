package org.xtimms.ridebus.ui.base.controller

import com.google.android.material.tabs.TabLayout

interface TabbedController {

    fun configureTabs(tabs: TabLayout) {}

    fun cleanupTabs(tabs: TabLayout) {}
}