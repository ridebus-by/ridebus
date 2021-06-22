package org.xtimms.ridebus.util.view

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.Window
import org.xtimms.ridebus.util.system.InternalResourceHelper
import org.xtimms.ridebus.util.system.getResourceColor

fun Window.setNavigationBarTransparentCompat(context: Context) {
    navigationBarColor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
        !InternalResourceHelper.getBoolean(context, "config_navBarNeedsScrim", true)
    ) {
        Color.TRANSPARENT
    } else {
        // Set navbar scrim 70% of navigationBarColor
        context.getResourceColor(android.R.attr.navigationBarColor, 0.7F)
    }
}