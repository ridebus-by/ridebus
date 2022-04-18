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
import org.xtimms.ridebus.widget.RideBusChipGroup

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

            val tags = ArrayList<RideBusChipGroup.ChipModel>()
            if (route.qrCode == 1) {
                tags += RideBusChipGroup.ChipModel(
                    title = view.context.getString(R.string.qr),
                    icon = R.drawable.ic_qr_code
                )
            }
            if (route.cash == 1) {
                tags += RideBusChipGroup.ChipModel(
                    title = view.context.getString(R.string.in_cash),
                    icon = R.drawable.ic_account_balance_wallet
                )
            }
            if (route.isSmall == 1) {
                tags += RideBusChipGroup.ChipModel(
                    title = view.context.getString(R.string.small_class),
                    icon = R.drawable.ic_little_class
                )
            }
            if (route.isBig == 1) {
                tags += RideBusChipGroup.ChipModel(
                    title = view.context.getString(R.string.big_class),
                    icon = R.drawable.ic_big_class
                )
            }
            if (route.isVeryBig == 1) {
                tags += RideBusChipGroup.ChipModel(
                    title = view.context.getString(R.string.very_big_class),
                    icon = R.drawable.ic_very_big_class
                )
            }

            // Manga info section
            binding.routeSummary.setTags(tags)
            binding.routeSummary.isVisible = route.following.isNotBlank()
            binding.routeSummary.description = view.context.getString(R.string.route_direction) +
                ": " + route.following + "\n\n" + view.context.getString(R.string.working_hours) +
                ": " + route.workingHours + "\n\n" + view.context.getString(R.string.additional_info) +
                ": " + route.techInfo + "\n\n" + view.context.getString(R.string.carrier_company) +
                ": " + route.carrierCompany
        }
    }
}
