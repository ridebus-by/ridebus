package org.xtimms.ridebus.ui.setting.search

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import org.xtimms.ridebus.R

class SettingsSearchItem(
    val settingsSearchResult: SettingsSearchHelper.SettingsSearchResult,
    val results: List<SettingsSearchItem>?
) :
    AbstractFlexibleItem<SettingsSearchHolder>() {

    override fun getLayoutRes(): Int {
        return R.layout.settings_search_controller_card
    }

    /**
     * Create view holder (see [SettingsSearchAdapter].
     *
     * @return holder of view.
     */
    override fun createViewHolder(
        view: View,
        adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>
    ): SettingsSearchHolder {
        return SettingsSearchHolder(view, adapter as SettingsSearchAdapter)
    }

    override fun bindViewHolder(
        adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>,
        holder: SettingsSearchHolder,
        position: Int,
        payloads: List<Any?>?
    ) {
        holder.bind(this)
    }

    override fun equals(other: Any?): Boolean {
        if (other is SettingsSearchItem) {
            return settingsSearchResult == settingsSearchResult
        }
        return false
    }

    override fun hashCode(): Int {
        return settingsSearchResult.hashCode()
    }
}