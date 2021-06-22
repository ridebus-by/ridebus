package org.xtimms.ridebus.ui.setting.search

import android.view.View
import eu.davidea.viewholders.FlexibleViewHolder
import org.xtimms.ridebus.databinding.SettingsSearchControllerCardBinding
import kotlin.reflect.full.createInstance

class SettingsSearchHolder(view: View, val adapter: SettingsSearchAdapter) :
    FlexibleViewHolder(view, adapter) {

    private val binding = SettingsSearchControllerCardBinding.bind(view)

    init {
        binding.titleWrapper.setOnClickListener {
            adapter.getItem(bindingAdapterPosition)?.let {
                val ctrl = it.settingsSearchResult.searchController::class.createInstance()
                ctrl.preferenceKey = it.settingsSearchResult.key

                // must pass a new Controller instance to avoid this error https://github.com/bluelinelabs/Conductor/issues/446
                adapter.titleClickListener.onTitleClick(ctrl)
            }
        }
    }

    /**
     * Show the loading of source search result.
     *
     * @param item item of card.
     */
    fun bind(item: SettingsSearchItem) {
        binding.searchResultPrefTitle.text = item.settingsSearchResult.title
        binding.searchResultPrefSummary.text = item.settingsSearchResult.summary
        binding.searchResultPrefBreadcrumb.text = item.settingsSearchResult.breadcrumb
    }
}
