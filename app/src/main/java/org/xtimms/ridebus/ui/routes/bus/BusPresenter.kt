package org.xtimms.ridebus.ui.routes.bus

import android.os.Bundle
import org.xtimms.ridebus.data.database.Dao
import org.xtimms.ridebus.ui.base.presenter.BasePresenter

open class BusPresenter : BasePresenter<BusController>() {

    private val db: Dao? = null

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        db?.getBuses()
    }

}