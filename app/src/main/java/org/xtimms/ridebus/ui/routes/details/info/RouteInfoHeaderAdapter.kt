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
                checkNotNull(drivingRouter).requestRoutes(
                    requestPoints,
                    drivingOptions,
                    vehicleOptions,
                    this
                )
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
            binding.noteChip.visibility =
                if (route.transportId == MINIBUS && preferences.isVisibleAttentionNote().get()) {
                    View.VISIBLE
                } else View.GONE

            binding.noteChip.clicks()
                .onEach { controller.showAttentionDialog() }
                .launchIn(controller.viewScope)

            setTransportTypeAttributes(route.transportId)

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

            val tags = listOf(
                route.qrCode to (R.string.qr to R.drawable.ic_qr_code),
                route.cash to (R.string.in_cash to R.drawable.ic_account_balance_wallet),
                route.isSmall to (R.string.small_class to R.drawable.ic_little_class),
                route.isBig to (R.string.big_class to R.drawable.ic_big_class),
                route.isVeryBig to (R.string.very_big_class to R.drawable.ic_very_big_class),
                route.isEco to (R.string.ecotransport to R.drawable.ic_eco),
                route.wifi to (R.string.wifi to R.drawable.ic_wifi),
                route.isLowFloor to (R.string.low_floor to R.drawable.ic_wheelchair)
            ).filter { it.first }
                .map { (_, pair) ->
                    RideBusChipGroup.ChipModel(
                        title = view.context.getString(pair.first),
                        icon = pair.second
                    )
                }

            // Route info section
            binding.routeSummary.setTags(tags)
            binding.routeSummary.description = """
                ${view.context.getString(R.string.route_direction)}: ${route.following}
    
                ${view.context.getString(R.string.working_hours)}: ${route.workingHours}
    
                ${view.context.getString(R.string.additional_info)}: ${route.techInfo}

                ${view.context.getString(R.string.carrier_company)}: ${route.carrierCompany}
            """.trimIndent()
        }

        private fun setTransportTypeAttributes(transportId: Int) {
            val (backgroundColorAttr, imageResource, colorFilterAttr) = when (transportId) {
                BUS -> Triple(
                    R.attr.busPrimaryContainer,
                    R.drawable.ic_bus_side,
                    R.attr.busOnPrimaryContainer
                )

                MINIBUS -> Triple(
                    R.attr.minibusPrimaryContainer,
                    R.drawable.ic_van_side,
                    R.attr.minibusOnPrimaryContainer
                )

                EXPRESS -> Triple(
                    R.attr.expressPrimaryContainer,
                    R.drawable.ic_lightning_bolt,
                    R.attr.expressOnPrimaryContainer
                )

                TRAM -> Triple(
                    R.attr.tramPrimaryContainer,
                    R.drawable.ic_tram,
                    R.attr.tramOnPrimaryContainer
                )

                else -> Triple(0, 0, 0)
            }

            if (backgroundColorAttr != 0) {
                binding.circleTransport.setBackgroundColor(
                    itemView.context.getThemeColor(
                        backgroundColorAttr
                    )
                )
            }
            if (imageResource != 0) {
                binding.type.setImageResource(imageResource)
            }
            if (colorFilterAttr != 0) {
                binding.type.setColorFilter(itemView.context.getThemeColor(colorFilterAttr))
            }
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
