package org.xtimms.ridebus.ui.routes.bus

import android.os.Bundle
import org.xtimms.ridebus.data.database.RideBusDatabase
import org.xtimms.ridebus.ui.base.presenter.BasePresenter
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class BusPresenter(
    private val db: RideBusDatabase = Injekt.get()
) : BasePresenter<BusController>() {

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
    }
}
