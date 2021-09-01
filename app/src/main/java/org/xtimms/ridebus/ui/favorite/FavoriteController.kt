package org.xtimms.ridebus.ui.favorite

import android.view.LayoutInflater
import android.view.View
import dev.chrisbanes.insetter.applyInsetter
import org.xtimms.ridebus.R
import org.xtimms.ridebus.databinding.FavoriteControllerBinding
import org.xtimms.ridebus.ui.base.controller.NucleusController
import org.xtimms.ridebus.ui.base.controller.RootController

class FavoriteController :
    NucleusController<FavoriteControllerBinding, FavoritePresenter>(),
    RootController {

    override fun getTitle(): String? {
        return resources?.getString(R.string.title_favorite)
    }

    override fun createBinding(inflater: LayoutInflater) = FavoriteControllerBinding.inflate(inflater)

    override fun createPresenter(): FavoritePresenter {
        return FavoritePresenter()
    }

    override fun onViewCreated(view: View) {
        super.onViewCreated(view)

        binding.recycler.applyInsetter {
            type(navigationBars = true) {
                padding()
            }
        }

        binding.emptyView.show(R.drawable.ic_favorite_off, R.string.information_no_favorite_stops)
    }
}
