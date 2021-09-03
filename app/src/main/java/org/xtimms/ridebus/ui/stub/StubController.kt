package org.xtimms.ridebus.ui.stub

import android.view.LayoutInflater
import android.view.View
import org.xtimms.ridebus.R
import org.xtimms.ridebus.databinding.StubControllerBinding
import org.xtimms.ridebus.ui.base.controller.NucleusController

//
// Created by Xtimms on 28.08.2021.
//
class StubController :
    NucleusController<StubControllerBinding, StubPresenter>() {

    override fun getTitle(): String? = "Stub"

    override fun createBinding(inflater: LayoutInflater) = StubControllerBinding.inflate(inflater)

    override fun createPresenter(): StubPresenter {
        return StubPresenter()
    }

    override fun onViewCreated(view: View) {
        super.onViewCreated(view)
        binding.emptyView.show(R.drawable.ic_bulldozer, "Work in progress")
    }
}
