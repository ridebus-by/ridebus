package org.xtimms.ridebus.ui.favourite

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractHeaderItem
import eu.davidea.flexibleadapter.items.IFlexible
import org.xtimms.ridebus.R

data class TypeItem(val type: String) : AbstractHeaderItem<TypeHolder>() {

    override fun getLayoutRes(): Int {
        return R.layout.section_header_item
    }

    override fun createViewHolder(
        view: View,
        adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>
    ): TypeHolder {
        return TypeHolder(view, adapter)
    }

    override fun bindViewHolder(
        adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>,
        holder: TypeHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        holder.bind(this)
    }
}
