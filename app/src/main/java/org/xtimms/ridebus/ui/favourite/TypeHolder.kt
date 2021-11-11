package org.xtimms.ridebus.ui.favourite

import android.view.View
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.viewholders.FlexibleViewHolder
import org.xtimms.ridebus.databinding.SectionHeaderItemBinding
import org.xtimms.ridebus.util.system.LocaleHelper

class TypeHolder(view: View, adapter: FlexibleAdapter<*>) :
    FlexibleViewHolder(view, adapter) {

    private val binding = SectionHeaderItemBinding.bind(view)

    fun bind(item: TypeItem) {
        binding.title.text = LocaleHelper.getTypeDisplayName(item.type, itemView.context)
    }
}
