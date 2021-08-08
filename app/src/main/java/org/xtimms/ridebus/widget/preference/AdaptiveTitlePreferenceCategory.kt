package org.xtimms.ridebus.widget.preference

import android.content.Context
import androidx.core.view.updateLayoutParams
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceViewHolder
import androidx.recyclerview.widget.RecyclerView

class AdaptiveTitlePreferenceCategory(context: Context) : PreferenceCategory(context) {
    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        if (title.isNullOrBlank()) {
            holder.itemView.updateLayoutParams<RecyclerView.LayoutParams> {
                height = 0
                topMargin = 0
            }
        }
    }
}
