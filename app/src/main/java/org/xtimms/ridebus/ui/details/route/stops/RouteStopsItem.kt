package org.xtimms.ridebus.ui.details.route.stops

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import org.xtimms.ridebus.R
import org.xtimms.ridebus.data.database.entity.Stop

class RouteStopsItem(val stop: Stop) :
    AbstractFlexibleItem<RouteStopsHolder>() {

    override fun getLayoutRes(): Int {
        return R.layout.route_stop_item
    }

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>): RouteStopsHolder {
        return RouteStopsHolder(view, adapter as RouteStopsAdapter)
    }

    override fun bindViewHolder(
        adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>,
        holder: RouteStopsHolder,
        position: Int,
        payloads: List<Any?>?
    ) {
        holder.bind(this)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other is RouteStopsItem) {
            return stop.stopId!! == other.stop.stopId!!
        }
        return false
    }

    override fun hashCode(): Int {
        return stop.stopId!!.hashCode()
    }
}
