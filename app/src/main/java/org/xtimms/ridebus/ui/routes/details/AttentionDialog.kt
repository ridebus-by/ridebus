package org.xtimms.ridebus.ui.routes.details

import android.app.Dialog
import android.os.Bundle
import com.bluelinelabs.conductor.Controller
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.xtimms.ridebus.R
import org.xtimms.ridebus.ui.base.controller.DialogController

class AttentionDialog(bundle: Bundle? = null) : DialogController(bundle) {

    constructor(
        target: Controller
    ) : this() {
        targetController = target
    }

    override fun onCreateDialog(savedViewState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(checkNotNull(activity), com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered)
            .setIcon(activity?.getDrawable(R.drawable.ic_dissatisfied))
            .setTitle(activity?.getString(R.string.attention_title))
            .setMessage(activity?.getString(R.string.attention_message))
            .setPositiveButton(activity?.getString(android.R.string.ok)) { _, _ ->
                dismissDialog()
            }
            .setCancelable(true)
            .create()
    }
}
