package org.xtimms.ridebus.ui.base.activity

import androidx.viewbinding.ViewBinding
import org.xtimms.ridebus.util.system.LocaleHelper

abstract class BaseViewBindingActivity<VB : ViewBinding> : BaseThemedActivity() {

    lateinit var binding: VB

    init {
        @Suppress("LeakingThis")
        LocaleHelper.updateConfiguration(this)
    }
}
