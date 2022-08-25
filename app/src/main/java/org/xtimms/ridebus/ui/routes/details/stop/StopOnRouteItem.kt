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
import org.xtimms.ridebus.util.Times

class StopOnRouteItem(stop: Stop, val times: Times) :
    BaseStopItem<StopOnRouteHolder, AbstractHeaderItem<FlexibleViewHolder>>(stop) {

    override fun getLayoutRes(): Int {
        return R.layout.route_detail_stop_item
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as StopOnRouteItem

        if (times != other.times) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + times.hashCode()
        return result
    }
}
