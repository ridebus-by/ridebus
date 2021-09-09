package org.xtimms.ridebus.ui.routes.details

import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import dev.chrisbanes.insetter.applyInsetter
import org.xtimms.ridebus.databinding.RoutesDetailControllerBinding
import org.xtimms.ridebus.ui.base.controller.NoToolbarElevationController
import org.xtimms.ridebus.ui.base.controller.NucleusController

class RouteDetailsController :
    NucleusController<RoutesDetailControllerBinding, RouteDetailsPresenter>(),
    NoToolbarElevationController {

    override fun getTitle(): String? {
        return "Stub"
    }

    override fun createBinding(inflater: LayoutInflater) = RoutesDetailControllerBinding.inflate(inflater)

    override fun createPresenter(): RouteDetailsPresenter {
        return RouteDetailsPresenter()
    }

    override fun onViewCreated(view: View) {
        super.onViewCreated(view)

        binding.fullRecycler.applyInsetter {
            type(navigationBars = true) {
                padding()
            }
        }
        binding.fullRecycler.layoutManager = LinearLayoutManager(view.context)
        binding.fullRecycler.setHasFixedSize(true)
    }

    companion object {
        const val ROUTE_EXTRA = "route"
    }
}
