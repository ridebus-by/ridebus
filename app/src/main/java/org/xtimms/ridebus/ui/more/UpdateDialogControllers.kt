package org.xtimms.ridebus.ui.more

import android.app.Dialog
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.TextView
import androidx.core.os.bundleOf
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.noties.markwon.Markwon
import org.xtimms.ridebus.R
import org.xtimms.ridebus.data.updater.AppUpdateResult
import org.xtimms.ridebus.data.updater.AppUpdateService
import org.xtimms.ridebus.ui.base.controller.DialogController
import org.xtimms.ridebus.ui.base.controller.openInBrowser
import com.google.android.material.R as materialR

class NewUpdateDialogController(bundle: Bundle? = null) : DialogController(bundle) {

    constructor(update: AppUpdateResult.NewUpdate) : this(
        bundleOf(
            BODY_KEY to update.release.info,
            RELEASE_URL_KEY to update.release.releaseLink,
            DOWNLOAD_URL_KEY to update.release.getDownloadLink()
        )
    )

    override fun onCreateDialog(savedViewState: Bundle?): Dialog {
        val releaseBody = checkNotNull(args.getString(BODY_KEY))
            .replace("""---(\R|.)*Checksums(\R|.)*""".toRegex(), "")
        val info = Markwon.create(checkNotNull(activity)).toMarkdown(releaseBody)

        return MaterialAlertDialogBuilder(
            checkNotNull(activity),
            materialR.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered
        )
            .setTitle(R.string.update_check_notification_update_available)
            .setMessage(info)
            .setIcon(R.drawable.ic_update)
            .setPositiveButton(R.string.update_check_confirm) { _, _ ->
                applicationContext?.let { context ->
                    // Start download
                    val url = checkNotNull(args.getString(DOWNLOAD_URL_KEY))
                    AppUpdateService.start(context, url)
                }
            }
            .setNeutralButton(R.string.update_check_open) { _, _ ->
                openInBrowser(checkNotNull(args.getString(RELEASE_URL_KEY)))
            }
            .setNeutralButton(R.string.action_postpone) { _, _ ->
                dialog?.dismiss()
            }
            .create()
    }

    override fun onAttach(view: View) {
        super.onAttach(view)

        // Make links in Markdown text clickable
        (dialog?.findViewById(android.R.id.message) as? TextView)?.movementMethod =
            LinkMovementMethod.getInstance()
    }
}

private const val BODY_KEY = "NewUpdateDialogController.body"
private const val RELEASE_URL_KEY = "NewUpdateDialogController.release_url"
private const val DOWNLOAD_URL_KEY = "NewUpdateDialogController.download_url"
