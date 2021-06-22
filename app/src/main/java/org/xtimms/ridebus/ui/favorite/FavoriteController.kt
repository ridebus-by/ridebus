package org.xtimms.ridebus.ui.favorite

import android.view.LayoutInflater
import org.xtimms.ridebus.R
import org.xtimms.ridebus.databinding.FavoriteControllerBinding
import org.xtimms.ridebus.databinding.StopsControllerBinding
import org.xtimms.ridebus.ui.base.controller.NucleusController
import org.xtimms.ridebus.ui.base.controller.RootController
import org.xtimms.ridebus.ui.stops.StopsPresenter

class FavoriteController :
    NucleusController<FavoriteControllerBinding, FavoritePresenter>(),
    RootController {

    private var currentTitle: String? = null
        set(value) {
            if (field != value) {
                field = value
                setTitle()
            }
        }

    override fun getTitle(): String? {
        return currentTitle ?: resources?.getString(R.string.title_favorite)
    }

    override fun createBinding(inflater: LayoutInflater) = FavoriteControllerBinding.inflate(inflater)

    override fun createPresenter(): FavoritePresenter {
        return FavoritePresenter()
    }


}