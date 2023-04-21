package org.xtimms.ridebus.ui.main.welcome

import android.os.Bundle
import kotlinx.coroutines.launch
import org.xtimms.ridebus.data.database.RideBusDatabase
import org.xtimms.ridebus.ui.base.presenter.BasePresenter
import org.xtimms.ridebus.util.lang.withUIContext
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class CityPresenter(
    private val db: RideBusDatabase = Injekt.get()
) : BasePresenter<WelcomeDialogController>() {

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        loadStops()
    }

    private fun loadStops() {
        presenterScope.launch {
            val cities = db.cityDao().getCities()
            withUIContext {
                view?.setList(cities.map(::CityItem))
            }
        }
    }
}
