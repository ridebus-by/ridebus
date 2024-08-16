package org.xtimms.ridebus.ui.routes

import android.os.Bundle
import kotlinx.coroutines.launch
import org.xtimms.ridebus.data.usecases.UseCases
import org.xtimms.ridebus.ui.base.presenter.BasePresenter
import org.xtimms.ridebus.util.lang.withUIContext
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class RoutePresenter(
    private val useCases: UseCases = Injekt.get()
) : BasePresenter<RouteController>() {

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        getRoutes()
    }

    private fun getRoutes() {
        presenterScope.launch {
            useCases.getRoutes().collect { response ->
                withUIContext {
                    view?.setRoutes(response.map(::RouteItem))
                }
            }
        }
    }
}
