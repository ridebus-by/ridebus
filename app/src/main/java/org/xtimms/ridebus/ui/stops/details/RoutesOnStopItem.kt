package org.xtimms.ridebus.ui.stops.details

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import org.xtimms.ridebus.R
import org.xtimms.ridebus.data.database.entity.Route
import org.xtimms.ridebus.util.Times

class RoutesOnStopItem(val route: Route, val times: Times) :
    AbstractFlexibleItem<RouteOnStopHolder>() {

    override fun getLayoutRes(): Int {
        return R.layout.stops_route_item
    }

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>): RouteOnStopHolder {
        return RouteOnStopHolder(view, adapter as RoutesOnStopAdapter)
    }

    override fun bindViewHolder(
        adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>,
        holder: RouteOnStopHolder,
        position: Int,
        payloads: List<Any?>?
    ) {
        holder.bind(this)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RoutesOnStopItem

        if (times != other.times) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + times.hashCode()
        return result
    }
}
