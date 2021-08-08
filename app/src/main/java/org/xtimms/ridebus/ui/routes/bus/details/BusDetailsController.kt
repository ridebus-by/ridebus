package org.xtimms.ridebus.ui.routes.bus.details

import android.view.LayoutInflater
import org.xtimms.ridebus.databinding.RoutesDetailControllerBinding
import org.xtimms.ridebus.ui.base.controller.RxController
import org.xtimms.ridebus.ui.base.controller.ToolbarLiftOnScrollController

class BusDetailsController() :
    RxController<RoutesDetailControllerBinding>(),
    ToolbarLiftOnScrollController {

    override fun createBinding(inflater: LayoutInflater) = RoutesDetailControllerBinding.inflate(inflater)
}
