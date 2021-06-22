package org.xtimms.ridebus.ui.stops

import android.view.LayoutInflater
import org.xtimms.ridebus.R
import org.xtimms.ridebus.databinding.StopsControllerBinding
import org.xtimms.ridebus.ui.base.controller.NucleusController
import org.xtimms.ridebus.ui.base.controller.RootController

class StopsController :
    NucleusController<StopsControllerBinding, StopsPresenter>(),
    RootController {

    private var currentTitle: String? = null
        set(value) {
            if (field != value) {
                field = value
                setTitle()
            }
        }

    override fun getTitle(): String? {
        return currentTitle ?: resources?.getString(R.string.title_stops)
    }

    override fun createBinding(inflater: LayoutInflater) = StopsControllerBinding.inflate(inflater)

    override fun createPresenter(): StopsPresenter {
        return StopsPresenter()
    }


}