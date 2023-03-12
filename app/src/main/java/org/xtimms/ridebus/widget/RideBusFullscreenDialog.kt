package org.xtimms.ridebus.widget

import android.content.Context
import android.view.View
import androidx.appcompat.app.AppCompatDialog
import org.xtimms.ridebus.R

class RideBusFullscreenDialog(context: Context, view: View) : AppCompatDialog(context, R.style.ThemeOverlay_RideBus_Dialog_Fullscreen) {

    init {
        setContentView(view)
    }
}
