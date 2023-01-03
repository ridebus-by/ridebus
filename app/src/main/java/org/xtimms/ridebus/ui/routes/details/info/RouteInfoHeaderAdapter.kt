package org.xtimms.ridebus.ui.routes.details.info

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.xtimms.ridebus.R
import org.xtimms.ridebus.data.database.entity.Route
import org.xtimms.ridebus.data.preference.PreferencesHelper
import org.xtimms.ridebus.databinding.RouteDetailHeaderBinding
import org.xtimms.ridebus.ui.base.controller.getMainAppBarHeight
import org.xtimms.ridebus.ui.routes.details.RouteDetailsController
import org.xtimms.ridebus.util.system.getThemeColor
import org.xtimms.ridebus.widget.RideBusChipGroup
import reactivecircus.flowbinding.android.view.clicks
import uy.kohesive.injekt.injectLazy

class RouteInfoHeaderAdapter(
    private val controller: RouteDetailsController,
    private val isTablet: Boolean
) :
    RecyclerView.Adapter<RouteInfoHeaderAdapter.HeaderViewHolder>() {

    private var route: Route = controller.presenter.route

    private lateinit var binding: RouteDetailHeaderBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder {
        binding = RouteDetailHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        updateDetailsPosition()

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

    private fun updateDetailsPosition() {
        if (isTablet) return
        val appBarHeight = controller.getMainAppBarHeight()
        binding.details.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            topMargin += appBarHeight
        }
    }

    inner class HeaderViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        private val preferences by injectLazy<PreferencesHelper>()

        fun bind() {
            setRouteInfo()
        }

        /**
         * Update the view with route information.
         */
        private fun setRouteInfo() {
            if (route.transportId == MINIBUS && preferences.isVisibleAttentionNote().get()) binding.noteChip.visibility = View.VISIBLE

            binding.noteChip.clicks()
                .onEach { controller.showAttentionDialog() }
                .launchIn(controller.viewScope)

            when (route.transportId) {
                BUS -> binding.circleTransport.setBackgroundColor(itemView.context.getThemeColor(R.attr.busPrimary))
                MINIBUS -> binding.circleTransport.setBackgroundColor(itemView.context.getThemeColor(R.attr.minibusPrimary))
                EXPRESS -> binding.circleTransport.setBackgroundColor(itemView.context.getThemeColor(R.attr.expressPrimary))
            }

            when (route.transportId) {
                BUS -> binding.type.setImageResource(R.drawable.ic_bus_side)
                MINIBUS -> binding.type.setImageResource(R.drawable.ic_van_side)
                EXPRESS -> binding.type.setImageResource(R.drawable.ic_lightning_bolt)
            }

            when (route.transportId) {
                BUS -> binding.type.setColorFilter(itemView.context.getThemeColor(R.attr.busOnPrimary))
                MINIBUS -> binding.type.setColorFilter(itemView.context.getThemeColor(R.attr.minibusOnPrimary))
                EXPRESS -> binding.type.setColorFilter(itemView.context.getThemeColor(R.attr.expressOnPrimary))
            }

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

            // Route info section
            binding.routeSummary.setTags(tags)
            binding.routeSummary.description = view.context.getString(R.string.route_direction) +
                ": " + route.following + "\n\n" + view.context.getString(R.string.working_hours) +
                ": " + route.workingHours + "\n\n" + view.context.getString(R.string.additional_info) +
                ": " + route.techInfo + "\n\n" + view.context.getString(R.string.carrier_company) +
                ": " + route.carrierCompany
        }
    }

    companion object {
        private const val BUS = 1
        private const val MINIBUS = 2
        private const val EXPRESS = 3
    }
}
