package org.xtimms.ridebus.ui.routes.bus

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import org.xtimms.ridebus.R
import org.xtimms.ridebus.data.database.Route

data class BusItem(val route: Route) :
    AbstractFlexibleItem<BusHolder>() {

    override fun getLayoutRes(): Int {
        return R.layout.routes_item
    }

    override fun createViewHolder(
        view: View,
        adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>
    ): BusHolder {
        return BusHolder(view, adapter as BusAdapter)
    }

    override fun bindViewHolder(
        adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>?,
        holder: BusHolder,
        position: Int,
        payloads: MutableList<Any>?
    ) {
        holder.bind(this)
    }

}