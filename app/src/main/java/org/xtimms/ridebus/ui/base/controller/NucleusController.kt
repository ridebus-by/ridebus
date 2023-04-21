package org.xtimms.ridebus.ui.base.controller

import android.os.Bundle
import androidx.viewbinding.ViewBinding
import nucleus.factory.PresenterFactory
import nucleus.presenter.Presenter
import org.xtimms.ridebus.ui.base.presenter.NucleusConductorDelegate
import org.xtimms.ridebus.ui.base.presenter.NucleusConductorLifecycleListener

@Suppress("LeakingThis")
abstract class NucleusController<VB : ViewBinding, P : Presenter<*>>(val bundle: Bundle? = null) :
    BaseController<VB>(bundle),
    PresenterFactory<P> {

    private val delegate = NucleusConductorDelegate(this)

    val presenter: P
        get() = delegate.presenter!!

    init {
        addLifecycleListener(NucleusConductorLifecycleListener(delegate))
    }
}
