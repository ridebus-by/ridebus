package org.xtimms.ridebus.ui.nearby

import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import dev.chrisbanes.insetter.applyInsetter
import org.xtimms.ridebus.R
import org.xtimms.ridebus.databinding.NearbyControllerBinding
import org.xtimms.ridebus.ui.base.controller.NucleusController
import org.xtimms.ridebus.util.system.toast
import org.xtimms.ridebus.widget.EmptyView

class NearbyController : // TODO
    NucleusController<NearbyControllerBinding, NearbyPresenter>() {

    override fun getTitle(): String? {
        return resources?.getString(R.string.near_me)
    }

    override fun createBinding(inflater: LayoutInflater) = NearbyControllerBinding.inflate(inflater)

    override fun createPresenter(): NearbyPresenter {
        return NearbyPresenter()
    }

    override fun onViewCreated(view: View) {
        super.onViewCreated(view)

        binding.recycler.applyInsetter {
            type(navigationBars = true) {
                padding()
            }
        }

        binding.recycler.layoutManager = LinearLayoutManager(view.context)
        binding.recycler.setHasFixedSize(true)

        binding.emptyView.show(
            R.drawable.ic_marker_alert,
            R.string.information_no_location_permission,
            listOf(
                EmptyView.Action(R.string.grant_permission, R.drawable.ic_marker_check) {
                    activity?.toast("KEKW")
                }
            ),
        )
    }
}
