package org.xtimms.ridebus.ui.routes

import org.xtimms.ridebus.data.preference.PreferencesHelper
import org.xtimms.ridebus.ui.base.presenter.BasePresenter
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class RoutesPresenter(
    private val preferences: PreferencesHelper = Injekt.get()
) : BasePresenter<RoutesController>() {

}