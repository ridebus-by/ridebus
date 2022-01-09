package org.xtimms.ridebus.ui.stops

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import org.xtimms.ridebus.R
import org.xtimms.ridebus.data.database.entity.Stop

//
// Created by Xtimms on 01.09.2021.
//
class StopsItem(val stop: Stop) : AbstractFlexibleItem<StopsHolder>() {

    override fun equals(other: Any?): Boolean {
        if (other is StopsItem) {
            return stop.stopId == other.stop.stopId
        }
        return false
    }

    override fun hashCode(): Int {
        return stop.stopId.hashCode()
    }

    override fun getLayoutRes(): Int {
        return R.layout.stops_item
    }

    override fun createViewHolder(
        view: View,
        adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>
    ): StopsHolder {
        return StopsHolder(view, adapter as StopsAdapter)
    }

    override fun bindViewHolder(
        adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>,
        holder: StopsHolder,
        position: Int,
        payloads: List<Any?>?
    ) {
        holder.bind(this)
    }
}
