package org.xtimms.ridebus.ui.routes.details.stop

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractHeaderItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import org.xtimms.ridebus.R
import org.xtimms.ridebus.data.database.entity.Stop
import org.xtimms.ridebus.ui.routes.details.stop.base.BaseStopItem

class StopOnRouteItem(stop: Stop) :
    BaseStopItem<StopOnRouteHolder, AbstractHeaderItem<FlexibleViewHolder>>(stop) {

    override fun getLayoutRes(): Int {
        return R.layout.route_stop_item
    }

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>): StopOnRouteHolder {
        return StopOnRouteHolder(view, adapter as StopsOnRouteAdapter)
    }

    override fun bindViewHolder(
        adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>,
        holder: StopOnRouteHolder,
        position: Int,
        payloads: List<Any?>?
    ) {
        holder.bind(this)
    }
}
