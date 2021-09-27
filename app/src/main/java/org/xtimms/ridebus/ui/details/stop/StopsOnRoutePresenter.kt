package org.xtimms.ridebus.ui.details.stop

import android.os.Bundle
import org.xtimms.ridebus.data.database.entity.Stop
import org.xtimms.ridebus.ui.base.presenter.BasePresenter
import rx.Observable
import rx.Subscription

class StopsOnRoutePresenter(val stop: Stop) : BasePresenter<StopsOnRouteController>() {

    /**
     * Subscription to send routes list to the view.
     */
    private var viewRoutesOnStopSubscription: Subscription? = null

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        sendRouteToView()
    }

    private fun sendRouteToView() {
        viewRoutesOnStopSubscription?.let { remove(it) }
        viewRoutesOnStopSubscription = Observable.just(stop)
            .subscribeLatestCache({ view, stop -> view.onNextStop(stop) })
    }
}
