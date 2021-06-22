package org.xtimms.ridebus.ui.setting.search

import android.os.Bundle
import org.xtimms.ridebus.data.preference.PreferencesHelper
import org.xtimms.ridebus.ui.base.presenter.BasePresenter
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

open class SettingsSearchPresenter : BasePresenter<SettingsSearchController>() {

    val preferences: PreferencesHelper = Injekt.get()

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        query = savedState?.getString(SettingsSearchPresenter::query.name) ?: "" // TODO - Some way to restore previous query?
    }

    override fun onSave(state: Bundle) {
        state.putString(SettingsSearchPresenter::query.name, query)
        super.onSave(state)
    }
}