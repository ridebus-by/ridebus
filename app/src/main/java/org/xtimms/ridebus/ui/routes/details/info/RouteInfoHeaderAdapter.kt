package org.xtimms.ridebus.ui.routes.details.info

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.RequestPointType
import com.yandex.mapkit.directions.DirectionsFactory
import com.yandex.mapkit.directions.driving.DrivingOptions
import com.yandex.mapkit.directions.driving.DrivingRoute
import com.yandex.mapkit.directions.driving.DrivingRouter
import com.yandex.mapkit.directions.driving.DrivingSession
import com.yandex.mapkit.directions.driving.VehicleOptions
import com.yandex.mapkit.geometry.BoundingBoxHelper
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polyline
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.runtime.Error
import com.yandex.runtime.network.NetworkError
import com.yandex.runtime.network.UnauthorizedError
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.xtimms.ridebus.R
import org.xtimms.ridebus.data.database.RideBusDatabase
import org.xtimms.ridebus.data.model.Route
import org.xtimms.ridebus.data.preference.PreferencesHelper
import org.xtimms.ridebus.databinding.RouteDetailHeaderBinding
import org.xtimms.ridebus.ui.base.controller.getMainAppBarHeight
import org.xtimms.ridebus.ui.main.MainActivity
import org.xtimms.ridebus.ui.routes.details.RouteDetailsController
import org.xtimms.ridebus.util.system.getThemeColor
import org.xtimms.ridebus.util.system.isNightMode
import org.xtimms.ridebus.util.view.snack
import org.xtimms.ridebus.widget.RideBusChipGroup
import reactivecircus.flowbinding.android.view.clicks
import uy.kohesive.injekt.injectLazy

class RouteInfoHeaderAdapter(
    private val controller: RouteDetailsController,
    private val isTablet: Boolean
) :
    RecyclerView.Adapter<RouteInfoHeaderAdapter.HeaderViewHolder>(),
    DrivingSession.DrivingRouteListener {

    private var route: Route = controller.presenter.route
    private val stopsPoints = mutableListOf<Point>()
    private var mapObjects: MapObjectCollection? = null
    private var drivingRouter: DrivingRouter? = null
    private var drivingSession: DrivingSession? = null
    private val db: RideBusDatabase by injectLazy()

    private lateinit var binding: RouteDetailHeaderBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder {
        binding =
            RouteDetailHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        updateDetailsPosition()

        MapKitFactory.initialize(parent.context)
        MapKitFactory.getInstance()

        val constraintLayout: ConstraintLayout = binding.root
        val constraintSet = ConstraintSet()

        if (db.routesAndStopsDao().getStopsCoordinates(route.routeId).isNotEmpty()) {
            for (coordinate in db.routesAndStopsDao().getStopsCoordinates(route.routeId)) {
                stopsPoints.add(
                    Point(
                        coordinate.latitude,
                        coordinate.longitude
                    )
                )
            }

            val boundsHelper = BoundingBoxHelper.getBounds(Polyline(stopsPoints))

            val cameraPosition = binding.mapView.map.cameraPosition(boundsHelper)

            constraintSet.clone(constraintLayout)
            constraintSet.connect(
                R.id.backdrop_overlay,
                ConstraintSet.BOTTOM,
                R.id.map_view,
                ConstraintSet.BOTTOM,
                0
            )
            constraintSet.applyTo(constraintLayout)
            binding.mapView.visibility = View.VISIBLE
            binding.backdrop.visibility = View.GONE

            binding.mapView.map.apply {
                isNightModeEnabled = parent.context.isNightMode()
                isRotateGesturesEnabled = false
                isScrollGesturesEnabled = false
                isTiltGesturesEnabled = false
                isZoomGesturesEnabled = false
                isModelsEnabled = false
                move(
                    CameraPosition(
                        cameraPosition.target,
                        cameraPosition.zoom - 0.8f,
                        cameraPosition.azimuth,
                        cameraPosition.tilt
                    )
                )
            }

            drivingRouter = DirectionsFactory.getInstance().createDrivingRouter()
            mapObjects = binding.mapView.map.mapObjects.addCollection()
            val drivingOptions = DrivingOptions()
            val vehicleOptions = VehicleOptions()
            val requestPoints: ArrayList<RequestPoint> = ArrayList()

            for (stopLocation in stopsPoints) {
                requestPoints.add(
                    RequestPoint(
                        Point(
                            stopLocation.latitude,
                            stopLocation.longitude
                        ),
                        RequestPointType.WAYPOINT,
                        null
                    )
                )
            }

            drivingSession =
                checkNotNull(drivingRouter).requestRoutes(requestPoints, drivingOptions, vehicleOptions, this)
        } else {
            constraintSet.clone(constraintLayout)
            constraintSet.connect(
                R.id.backdrop_overlay,
                ConstraintSet.BOTTOM,
                R.id.backdrop,
                ConstraintSet.BOTTOM,
                0
            )
            constraintSet.applyTo(constraintLayout)
            binding.mapView.visibility = View.GONE
            binding.backdrop.visibility = View.VISIBLE
        }

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
            if (route.transportId == MINIBUS && preferences.isVisibleAttentionNote().get()) {
                binding.noteChip.visibility = View.VISIBLE
            }

            binding.noteChip.clicks()
                .onEach { controller.showAttentionDialog() }
                .launchIn(controller.viewScope)

            when (route.transportId) {
                BUS -> binding.circleTransport.setBackgroundColor(
                    itemView.context.getThemeColor(R.attr.busPrimaryContainer)
                )

                MINIBUS -> binding.circleTransport.setBackgroundColor(
                    itemView.context.getThemeColor(
                        R.attr.minibusPrimaryContainer
                    )
                )

                EXPRESS -> binding.circleTransport.setBackgroundColor(
                    itemView.context.getThemeColor(
                        R.attr.expressPrimaryContainer
                    )
                )

                TRAM -> binding.circleTransport.setBackgroundColor(
                    itemView.context.getThemeColor(R.attr.tramPrimaryContainer)
                )
            }

            when (route.transportId) {
                BUS -> binding.type.setImageResource(R.drawable.ic_bus_side)
                MINIBUS -> binding.type.setImageResource(R.drawable.ic_van_side)
                EXPRESS -> binding.type.setImageResource(R.drawable.ic_lightning_bolt)
                TRAM -> binding.type.setImageResource(R.drawable.ic_tram)
            }

            when (route.transportId) {
                BUS -> binding.type.setColorFilter(
                    itemView.context.getThemeColor(R.attr.busOnPrimaryContainer)
                )

                MINIBUS -> binding.type.setColorFilter(
                    itemView.context.getThemeColor(R.attr.minibusOnPrimaryContainer)
                )

                EXPRESS -> binding.type.setColorFilter(
                    itemView.context.getThemeColor(R.attr.expressOnPrimaryContainer)
                )

                TRAM -> binding.type.setColorFilter(
                    itemView.context.getThemeColor(R.attr.tramOnPrimaryContainer)
                )
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
            if (route.qrCode) {
                tags += RideBusChipGroup.ChipModel(
                    title = view.context.getString(R.string.qr),
                    icon = R.drawable.ic_qr_code
                )
            }
            if (route.cash) {
                tags += RideBusChipGroup.ChipModel(
                    title = view.context.getString(R.string.in_cash),
                    icon = R.drawable.ic_account_balance_wallet
                )
            }
            if (route.isSmall) {
                tags += RideBusChipGroup.ChipModel(
                    title = view.context.getString(R.string.small_class),
                    icon = R.drawable.ic_little_class
                )
            }
            if (route.isBig) {
                tags += RideBusChipGroup.ChipModel(
                    title = view.context.getString(R.string.big_class),
                    icon = R.drawable.ic_big_class
                )
            }
            if (route.isVeryBig) {
                tags += RideBusChipGroup.ChipModel(
                    title = view.context.getString(R.string.very_big_class),
                    icon = R.drawable.ic_very_big_class
                )
            }
            if (route.isEco) {
                tags += RideBusChipGroup.ChipModel(
                    title = view.context.getString(R.string.ecotransport),
                    icon = R.drawable.ic_eco
                )
            }
            if (route.wifi) {
                tags += RideBusChipGroup.ChipModel(
                    title = view.context.getString(R.string.wifi),
                    icon = R.drawable.ic_wifi
                )
            }
            if (route.isLowFloor) {
                tags += RideBusChipGroup.ChipModel(
                    title = view.context.getString(R.string.low_floor),
                    icon = R.drawable.ic_wheelchair
                )
            }

            // Route info section
            binding.routeSummary.setTags(tags)
            binding.routeSummary.description = view.context.getString(R.string.route_direction) +
                ": " + route.following + "\n\n" + view.context.getString(R.string.working_hours) +
                ": " + route.workingHours + "\n\n" + view.context.getString(
                R.string.additional_info
            ) +
                ": " + route.techInfo + "\n\n" + view.context.getString(
                R.string.carrier_company
            ) +
                ": " + route.carrierCompany
        }
    }

    override fun onViewAttachedToWindow(holder: HeaderViewHolder) {
        super.onViewAttachedToWindow(holder)
        MapKitFactory.getInstance().onStart()
        binding.mapView.onStart()
    }

    override fun onViewDetachedFromWindow(holder: HeaderViewHolder) {
        MapKitFactory.getInstance().onStop()
        binding.mapView.onStop()
        super.onViewDetachedFromWindow(holder)
    }

    override fun onDrivingRoutes(routes: MutableList<DrivingRoute>) {
        for (route in routes) {
            mapObjects?.addPolyline(route.geometry)
        }
    }

    override fun onDrivingRoutesError(error: Error) {
        var errorMessage: String? = controller.resources?.getString(R.string.unknown_error_message)
        when (error) {
            is UnauthorizedError -> {
                errorMessage = controller.resources?.getString(R.string.invalid_api_key)
            }

            is NetworkError -> {
                errorMessage = controller.resources?.getString(R.string.network_error_message)
            }
        }

        if (errorMessage != null) {
            (controller.activity as? MainActivity)?.binding?.rootCoordinator?.snack(errorMessage)
        }
    }

    companion object {
        private const val BUS = 1
        private const val MINIBUS = 2
        private const val EXPRESS = 3
        private const val TRAM = 4
    }
}
