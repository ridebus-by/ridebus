package org.xtimms.ridebus.ui.base.presenter

import android.os.Bundle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import nucleus.presenter.RxPresenter
import rx.Observable
import kotlin.coroutines.CoroutineContext

open class BasePresenter<V> : RxPresenter<V>(), CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext = job + Dispatchers.IO

    lateinit var presenterScope: CoroutineScope

    /**
     * Query from the view where applicable
     */
    var query: String = ""

    override fun onCreate(savedState: Bundle?) {
        try {
            super.onCreate(savedState)
            presenterScope = MainScope()
        } catch (e: NullPointerException) {
            // Swallow this error. This should be fixed in the library but since it's not critical
            // (only used by restartables) it should be enough. It saves me a fork.
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenterScope.cancel()
    }

    // We're trying to avoid using Rx, so we "undeprecate" this
    @Suppress("DEPRECATION")
    override fun getView(): V? {
        return super.getView()
    }

    /**
     * Subscribes an observable with [deliverLatestCache] and adds it to the presenter's lifecycle
     * subscription list.
     *
     * @param onNext function to execute when the observable emits an item.
     * @param onError function to execute when the observable throws an error.
     */
    fun <T> Observable<T>.subscribeLatestCache(onNext: (V, T) -> Unit, onError: ((V, Throwable) -> Unit) = { _, _ -> }) = compose(deliverLatestCache<T>()).subscribe(split(onNext, onError)).apply { add(this) }
}
