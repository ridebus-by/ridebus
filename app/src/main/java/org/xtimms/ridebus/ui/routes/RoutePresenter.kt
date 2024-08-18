package org.xtimms.ridebus.ui.routes

import android.os.Bundle
import kotlinx.coroutines.launch
import org.xtimms.ridebus.data.preference.PreferencesHelper
import org.xtimms.ridebus.data.usecases.UseCases
import org.xtimms.ridebus.ui.base.presenter.BasePresenter
import org.xtimms.ridebus.util.lang.withUIContext
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class RoutePresenter(
    private val transportType: Int,
    private val preferences: PreferencesHelper = Injekt.get(),
    private val useCases: UseCases = Injekt.get()
) : BasePresenter<RouteController>() {

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        getRoutes()
    }

    private fun getRoutes() {
        val cityId = preferences.city().get().toInt()
        presenterScope.launch {
            useCases.getRoutes(transportType, cityId).collect { response ->
                withUIContext {
                    view?.setRoutes(response.map(::RouteItem))
                }
            }
        }
    }
}
