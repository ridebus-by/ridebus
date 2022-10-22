package org.xtimms.ridebus.ui.more

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import org.xtimms.ridebus.BuildConfig
import org.xtimms.ridebus.R
import org.xtimms.ridebus.data.preference.PreferencesHelper
import org.xtimms.ridebus.util.CrashLogUtil
import org.xtimms.ridebus.util.system.copyToClipboard
import uy.kohesive.injekt.injectLazy

class AboutHeaderPreference @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : Preference(context, attrs) {

    private val preferences: PreferencesHelper by injectLazy()

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)

        val appName = holder.findViewById(R.id.appName) as TextView
        appName.text = if (BuildConfig.DEBUG) {
            "${context.getString(R.string.app_name)} Preview"
        } else {
            "${context.getString(R.string.app_name)} Stable"
        }

        val version = holder.findViewById(R.id.version) as TextView
        version.text = if (BuildConfig.DEBUG) {
            "r${BuildConfig.COMMIT_COUNT}"
        } else {
            "v${BuildConfig.VERSION_NAME}"
        }

        version.setOnClickListener {
            val deviceInfo = CrashLogUtil(context).getDebugInfo()
            context.copyToClipboard("Debug information", deviceInfo)
        }
    }

    init {
        layoutResource = R.layout.pref_about_header
        isSelectable = false
    }
}
