package org.xtimms.ridebus.ui.base.activity

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.xtimms.ridebus.data.preference.PreferencesHelper
import org.xtimms.ridebus.util.system.prepareTabletUiContext
import uy.kohesive.injekt.injectLazy

open class BaseActivity :
    AppCompatActivity(),
    ThemingDelegate by ThemingDelegateImpl() {

    protected val preferences: PreferencesHelper by injectLazy()

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(newBase.prepareTabletUiContext())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        applyAppTheme(this)
        super.onCreate(savedInstanceState)
    }
}
