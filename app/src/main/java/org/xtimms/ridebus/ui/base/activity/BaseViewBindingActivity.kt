package org.xtimms.ridebus.ui.base.activity

import android.content.Context
import androidx.viewbinding.ViewBinding
import org.xtimms.ridebus.util.system.LocaleHelper

abstract class BaseViewBindingActivity<VB : ViewBinding> : BaseThemedActivity() {

    lateinit var binding: VB

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.createLocaleWrapper(newBase))
    }
}
