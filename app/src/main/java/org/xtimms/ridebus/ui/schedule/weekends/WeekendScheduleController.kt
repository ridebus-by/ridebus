package org.xtimms.ridebus.ui.schedule.weekends

import android.view.LayoutInflater
import android.view.View
import dev.chrisbanes.insetter.applyInsetter
import eu.davidea.flexibleadapter.FlexibleAdapter
import org.xtimms.ridebus.R
import org.xtimms.ridebus.databinding.ScheduleControllerBinding
import org.xtimms.ridebus.ui.base.controller.NucleusController
import org.xtimms.ridebus.ui.stub.StubPresenter

class WeekendScheduleController :
    NucleusController<ScheduleControllerBinding, StubPresenter>(),
    FlexibleAdapter.OnUpdateListener {

    override fun createBinding(inflater: LayoutInflater) = ScheduleControllerBinding.inflate(inflater)

    override fun createPresenter(): StubPresenter {
        return StubPresenter()
    }

    override fun onViewCreated(view: View) {
        super.onViewCreated(view)

        binding.recycler.applyInsetter {
            type(navigationBars = true) {
                padding()
            }
        }
    }

    override fun onUpdateEmptyView(size: Int) {
        if (size > 0) {
            binding.emptyView.hide()
        } else {
            binding.emptyView.show(R.drawable.ic_alert, R.string.information_no_schedule_on_route)
        }
    }
}
