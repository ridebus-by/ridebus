package org.xtimms.ridebus.ui.main

import android.app.Dialog
import android.os.Bundle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.xtimms.ridebus.BuildConfig
import org.xtimms.ridebus.R
import org.xtimms.ridebus.data.updater.app.RELEASE_URL
import org.xtimms.ridebus.ui.base.controller.DialogController
import org.xtimms.ridebus.ui.base.controller.openInBrowser

class WhatsNewDialogController(bundle: Bundle? = null) : DialogController(bundle) {

    override fun onCreateDialog(savedViewState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(activity!!)
            .setTitle(activity!!.getString(R.string.updated_version, BuildConfig.VERSION_NAME))
            .setPositiveButton(android.R.string.ok, null)
            .setNeutralButton(R.string.whats_new) { _, _ ->
                openInBrowser(RELEASE_URL)
            }
            .create()
    }
}
