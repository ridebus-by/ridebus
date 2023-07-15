package org.xtimms.ridebus.ui.favourite

import android.app.Dialog
import android.os.Bundle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.xtimms.ridebus.ui.base.controller.DialogController

class FavouriteOptionsDialog(bundle: Bundle? = null) : DialogController(bundle) {

    private lateinit var favourite: String
    private lateinit var items: List<Pair<String, () -> Unit>>

    constructor(favourite: String, items: List<Pair<String, () -> Unit>>) : this() {
        this.favourite = favourite
        this.items = items
    }

    override fun onCreateDialog(savedViewState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(checkNotNull(activity))
            .setTitle(favourite)
            .setItems(items.map { it.first }.toTypedArray()) { dialog, which ->
                items[which].second()
                dialog.dismiss()
            }
            .create()
    }
}
