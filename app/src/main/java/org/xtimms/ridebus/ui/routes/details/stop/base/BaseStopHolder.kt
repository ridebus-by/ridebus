package org.xtimms.ridebus.ui.routes.details.stop.base

import android.view.View
import eu.davidea.viewholders.FlexibleViewHolder

open class BaseStopHolder(
    view: View,
    private val adapter: BaseStopsAdapter<*>
) : FlexibleViewHolder(view, adapter)
