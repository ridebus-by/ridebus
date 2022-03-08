package org.xtimms.ridebus.ui.routes.details.info

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import org.xtimms.ridebus.R
import org.xtimms.ridebus.data.database.entity.Route
import org.xtimms.ridebus.databinding.RouteInfoHeaderBinding
import org.xtimms.ridebus.ui.base.controller.getMainAppBarHeight
import org.xtimms.ridebus.ui.routes.details.RouteDetailsController

class RouteInfoHeaderAdapter(
    private val controller: RouteDetailsController,
    private val isTablet: Boolean,
) :
    RecyclerView.Adapter<RouteInfoHeaderAdapter.HeaderViewHolder>() {

    private var route: Route = controller.presenter.route

    private lateinit var binding: RouteInfoHeaderBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder {
        binding = RouteInfoHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        updateCoverPosition()

        binding.routeSummary.expanded = isTablet

        return HeaderViewHolder(binding.root)
    }

    override fun getItemCount(): Int = 1

    override fun getItemId(position: Int): Long = hashCode().toLong()

    override fun onBindViewHolder(holder: HeaderViewHolder, position: Int) {
        holder.bind()
    }

    /**
     * Update the view with route information.
     *
     * @param route route object containing information about route.
     */
    fun update(route: Route) {
        this.route = route
        update()
    }

    fun update() {
        notifyItemChanged(0, this)
    }

    private fun updateCoverPosition() {
        if (isTablet) return
        val appBarHeight = controller.getMainAppBarHeight()
        binding.number.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            topMargin += appBarHeight
        }
    }

    inner class HeaderViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun bind() {
            setRouteInfo()
        }

        /**
         * Update the view with route information.
         *
         * @param route route object containing information about route.
         */
        private fun setRouteInfo() {
            // Update number TextView
            binding.number.text = route.number

            // Update title TextView.
            binding.title.text = route.title.ifBlank {
                view.context.getString(R.string.unknown)
            }

            // Update route path TextView.
            binding.path.text = route.description.ifBlank {
                view.context.getString(R.string.unknown)
            }

            binding.fare.text = route.fare

            // Manga info section
            binding.routeSummary.isVisible = route.following.isNotBlank()
            binding.routeSummary.description = view.context.getString(R.string.route_direction) +
                ": " + route.following + "\n\n" + view.context.getString(R.string.payment_methods) +
                ": " + route.paymentMethods + "\n\n" + view.context.getString(R.string.additional_info) +
                ": " + route.techInfo
        }
    }
}
