package org.xtimms.ridebus.ui.more

import android.content.Context
import android.util.AttributeSet
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import org.xtimms.ridebus.R
import org.xtimms.ridebus.util.system.openInBrowser
import org.xtimms.ridebus.util.view.setTooltip

class AboutLinksPreference @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    Preference(context, attrs) {

    init {
        layoutResource = R.layout.pref_about_links
        isSelectable = false
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)

        holder.findViewById(R.id.btn_telegram).apply {
            setTooltip(contentDescription.toString())
            setOnClickListener { context.openInBrowser("https://t.me/ridebus") }
        }
        holder.findViewById(R.id.btn_vk).apply {
            setTooltip(contentDescription.toString())
        }
        holder.findViewById(R.id.btn_discord).apply {
            setTooltip(contentDescription.toString())
        }
        holder.findViewById(R.id.btn_github).apply {
            setTooltip(contentDescription.toString())
            setOnClickListener { context.openInBrowser("https://github.com/ztimms73/RideBus") }
        }
    }
}
