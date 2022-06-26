package org.xtimms.ridebus.ui.routes

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import org.xtimms.ridebus.R
import org.xtimms.ridebus.data.database.entity.Route

class RouteItem(val route: Route) : AbstractFlexibleItem<RouteHolder>() {

    override fun equals(other: Any?): Boolean {
        if (other is RouteItem) {
            return route.routeId == other.route.routeId
        }
        return false
    }

    override fun hashCode(): Int {
        return route.routeId.hashCode()
    }

    override fun getLayoutRes(): Int {
        return R.layout.route_item
    }

    override fun createViewHolder(
        view: View,
        adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>
    ): RouteHolder {
        return RouteHolder(view, adapter as RouteAdapter)
    }

    override fun bindViewHolder(
        adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>,
        holder: RouteHolder,
        position: Int,
        payloads: List<Any?>?
    ) {
        holder.bind(this)
    }
}
