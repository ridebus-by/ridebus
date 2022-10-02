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
import org.xtimms.ridebus.data.updater.app.AppUpdateResult
import org.xtimms.ridebus.data.updater.app.AppUpdateService
import org.xtimms.ridebus.data.updater.database.DatabaseUpdateResult
import org.xtimms.ridebus.data.updater.database.DatabaseUpdateService
import org.xtimms.ridebus.ui.base.controller.DialogController
import org.xtimms.ridebus.ui.base.controller.openInBrowser
import com.google.android.material.R as materialR

class NewUpdateDialogController(bundle: Bundle? = null) : DialogController(bundle) {

    constructor(update: AppUpdateResult.NewUpdate) : this(
        bundleOf(
            BODY_KEY to update.release.info,
            RELEASE_URL_KEY to update.release.releaseLink,
            DOWNLOAD_URL_KEY to update.release.getDownloadLink(),
        ),
    )

    override fun onCreateDialog(savedViewState: Bundle?): Dialog {
        val releaseBody = args.getString(BODY_KEY)!!
            .replace("""---(\R|.)*Checksums(\R|.)*""".toRegex(), "")
        val info = Markwon.create(activity!!).toMarkdown(releaseBody)

        return MaterialAlertDialogBuilder(activity!!, materialR.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered)
            .setTitle(R.string.update_check_notification_update_available)
            .setMessage(info)
            .setIcon(R.drawable.ic_update)
            .setPositiveButton(R.string.update_check_confirm) { _, _ ->
                applicationContext?.let { context ->
                    // Start download
                    val url = args.getString(DOWNLOAD_URL_KEY)!!
                    AppUpdateService.start(context, url)
                }
            }
            .setNeutralButton(R.string.update_check_open) { _, _ ->
                openInBrowser(args.getString(RELEASE_URL_KEY)!!)
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

class NewScheduleDialogController(bundle: Bundle? = null) : DialogController(bundle) {

    constructor(update: DatabaseUpdateResult.NewUpdate) : this(
        bundleOf(
            NEW_SCHEDULE_BODY_KEY to update.update.info,
            NEW_SCHEDULE_VERSION_KEY to update.update.version,
            NEW_SCHEDULE_DOWNLOAD_URL_KEY to update.update.getDownloadLink(),
        ),
    )

    override fun onCreateDialog(savedViewState: Bundle?): Dialog {
        val infoBody = args.getString(NEW_SCHEDULE_BODY_KEY)!!
        val version = args.getString(NEW_SCHEDULE_VERSION_KEY)!!

        val message = buildString {
            append(activity!!.getString(R.string.new_version_s, version))
            appendLine()
            appendLine()
            append(infoBody)
        }

        return MaterialAlertDialogBuilder(activity!!, materialR.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered)
            .setTitle(R.string.update_check_notification_database_update_available)
            .setMessage(message)
            .setIcon(R.drawable.ic_database_update)
            .setPositiveButton(R.string.update_check_confirm) { _, _ ->
                applicationContext?.let { context ->
                    // Start download
                    val url = args.getString(NEW_SCHEDULE_DOWNLOAD_URL_KEY)!!
                    val version = args.getString(NEW_SCHEDULE_VERSION_KEY)!!
                    DatabaseUpdateService.start(context, url, version = version)
                }
            }
            .create()
    }
}

private const val NEW_SCHEDULE_BODY_KEY = "NewScheduleDialogController.body"
private const val NEW_SCHEDULE_DOWNLOAD_URL_KEY = "NewScheduleDialogController.download_url"
private const val NEW_SCHEDULE_VERSION_KEY = "NewScheduleDialogController.version"
