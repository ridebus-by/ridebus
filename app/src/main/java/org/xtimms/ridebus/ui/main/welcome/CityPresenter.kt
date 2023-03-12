package org.xtimms.ridebus.ui.main.welcome

import android.os.Bundle
import org.xtimms.ridebus.data.database.RideBusDatabase
import org.xtimms.ridebus.data.database.entity.City
import org.xtimms.ridebus.ui.base.presenter.BasePresenter
import rx.Observable
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class CityPresenter(
    private val db: RideBusDatabase = Injekt.get()
) : BasePresenter<WelcomeDialogController>() {

    private var cities: List<City> = emptyList()

    private var stopsSubscription: Subscription? = null

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        loadStops()
    }

    private fun loadStops() {
        stopsSubscription?.unsubscribe()
        stopsSubscription = Observable.just(db.cityDao().getCities())
            .doOnNext { cities = it }
            .map { it.map(::CityItem) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeLatestCache(WelcomeDialogController::setList)
    }
}
