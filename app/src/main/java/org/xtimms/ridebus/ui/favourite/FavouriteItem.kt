package org.xtimms.ridebus.ui.favourite

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractSectionableItem
import eu.davidea.flexibleadapter.items.IFlexible
import org.xtimms.ridebus.R
import org.xtimms.ridebus.data.database.entity.Route

data class FavouriteItem(
    val route: Route,
    val header: TypeItem? = null,
    val isPinned: Boolean = false
) :
    AbstractSectionableItem<FavouriteHolder, TypeItem>(header) {

    override fun equals(other: Any?): Boolean {
        if (other is FavouriteItem) {
            return route.routeId == other.route.routeId &&
                getHeader()?.type == other.getHeader()?.type &&
                isPinned == other.isPinned
        }
        return false
    }

    override fun getLayoutRes(): Int {
        return R.layout.favourite_item
    }

    override fun createViewHolder(
        view: View,
        adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>
    ): FavouriteHolder {
        return FavouriteHolder(view, adapter as FavouritesAdapter)
    }

    override fun bindViewHolder(
        adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>,
        holder: FavouriteHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        holder.bind(this)
    }

    override fun hashCode(): Int {
        var result = route.routeId.hashCode()
        result = 31 * result + (header?.hashCode() ?: 0)
        result = 31 * result + isPinned.hashCode()
        return result
    }
}
