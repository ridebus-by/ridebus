package org.xtimms.ridebus.ui.routes.bus.details

import android.os.Bundle
import android.view.LayoutInflater
import org.xtimms.ridebus.databinding.RoutesDetailControllerBinding
import org.xtimms.ridebus.ui.base.controller.NucleusController
import org.xtimms.ridebus.ui.base.controller.RxController
import org.xtimms.ridebus.ui.base.controller.ToolbarLiftOnScrollController

class BusDetailsController() :
    RxController<RoutesDetailControllerBinding>(),
    ToolbarLiftOnScrollController {

    override fun createBinding(inflater: LayoutInflater) = RoutesDetailControllerBinding.inflate(inflater)

}