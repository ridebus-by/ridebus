package org.xtimms.ridebus.ui.routes

import android.os.Bundle
import android.view.LayoutInflater
import org.xtimms.ridebus.data.preference.PreferencesHelper
import org.xtimms.ridebus.databinding.RoutesControllerBinding
import org.xtimms.ridebus.ui.base.controller.RootController
import org.xtimms.ridebus.ui.base.controller.SearchableNucleusController
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class RoutesController(
    bundle: Bundle? = null,
    private val preferences: PreferencesHelper = Injekt.get()
) : SearchableNucleusController<RoutesControllerBinding, RoutesPresenter>(bundle),
    RootController {

    override fun createBinding(inflater: LayoutInflater) = RoutesControllerBinding.inflate(inflater)

    override fun createPresenter(): RoutesPresenter {
        return RoutesPresenter()
    }

}