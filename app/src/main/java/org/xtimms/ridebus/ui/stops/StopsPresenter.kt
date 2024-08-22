package org.xtimms.ridebus.ui.stops

import android.os.Bundle
import kotlinx.coroutines.launch
import org.xtimms.ridebus.data.preference.PreferencesHelper
import org.xtimms.ridebus.data.usecases.UseCases
import org.xtimms.ridebus.ui.base.presenter.BasePresenter
import org.xtimms.ridebus.util.lang.withUIContext
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class StopsPresenter(
    private val preferences: PreferencesHelper = Injekt.get(),
    private val useCases: UseCases = Injekt.get()
) : BasePresenter<StopsController>() {

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        loadStops(preferences.city().get().toInt())
    }

    private fun loadStops(cityId: Int) {
        presenterScope.launch {
            useCases.getStops(cityId).collect { response ->
                withUIContext {
                    view?.setStops(response.map(::StopsItem))
                }
            }
        }
    }
}
