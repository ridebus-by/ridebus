package org.xtimms.ridebus.ui.stops.details

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import org.xtimms.ridebus.R
import org.xtimms.ridebus.data.database.entity.Route

class RoutesOnStopItem(val route: Route) :
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
        if (other is RoutesOnStopItem) {
            return route.routeId == other.route.routeId
        }
        return false
    }

    override fun hashCode(): Int {
        return route.routeId.hashCode()
    }
}
