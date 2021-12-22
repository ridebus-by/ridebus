package org.xtimms.ridebus.util.view

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.Window
import com.google.android.material.elevation.ElevationOverlayProvider
import org.xtimms.ridebus.util.system.InternalResourceHelper
import org.xtimms.ridebus.util.system.getResourceColor

/**
 * Sets navigation bar color to transparent if system's config_navBarNeedsScrim is false,
 * otherwise it will use the theme navigationBarColor with 70% opacity.
 */
fun Window.setNavigationBarTransparentCompat(context: Context, elevation: Float = 0F) {
    navigationBarColor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
        !InternalResourceHelper.getBoolean(context, "config_navBarNeedsScrim", true)
    ) {
        Color.TRANSPARENT
    } else {
        // Set navbar scrim 70% of navigationBarColor
        ElevationOverlayProvider(context).compositeOverlayIfNeeded(
            context.getResourceColor(android.R.attr.navigationBarColor, 0.7F),
            elevation
        )
    }
}
