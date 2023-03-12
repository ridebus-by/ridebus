package org.xtimms.ridebus.ui.main.welcome

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import org.xtimms.ridebus.R
import org.xtimms.ridebus.data.database.entity.City

class CityItem(val city: City) : AbstractFlexibleItem<CityHolder>() {

    override fun equals(other: Any?): Boolean {
        if (other is CityItem) {
            return city.cityId == other.city.cityId
        }
        return false
    }

    override fun hashCode(): Int {
        return city.cityId.hashCode()
    }

    override fun getLayoutRes(): Int {
        return R.layout.stops_item
    }

    override fun createViewHolder(
        view: View,
        adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>
    ): CityHolder {
        return CityHolder(view, adapter as CityAdapter)
    }

    override fun bindViewHolder(
        adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>,
        holder: CityHolder,
        position: Int,
        payloads: List<Any?>?
    ) {
        holder.bind(this)
    }
}
