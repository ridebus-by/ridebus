package org.xtimms.ridebus.ui.more

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import org.xtimms.ridebus.BuildConfig
import org.xtimms.ridebus.R

class MoreHeaderPreference @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : Preference(context, attrs) {

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)

        val version = holder.findViewById(R.id.textView_subtitle) as TextView
        version.text = if (BuildConfig.DEBUG) {
            "Debug r${BuildConfig.COMMIT_COUNT} (${BuildConfig.COMMIT_SHA})"
        } else {
            "Release v${BuildConfig.VERSION_NAME} (${BuildConfig.COMMIT_SHA})"
        }
    }

    init {
        layoutResource = R.layout.more_header
        isSelectable = false
    }
}
