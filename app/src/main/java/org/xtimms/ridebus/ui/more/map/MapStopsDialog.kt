package org.xtimms.ridebus.ui.more.map

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.core.graphics.ColorUtils
import androidx.core.view.WindowCompat
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.IconStyle
import com.yandex.runtime.image.ImageProvider
import dev.chrisbanes.insetter.applyInsetter
import org.xtimms.ridebus.R
import org.xtimms.ridebus.data.database.RideBusDatabase
import org.xtimms.ridebus.data.preference.PreferencesHelper
import org.xtimms.ridebus.databinding.MapStopsDialogBinding
import org.xtimms.ridebus.ui.base.controller.DialogController
import org.xtimms.ridebus.util.system.isNightMode
import org.xtimms.ridebus.util.view.setNavigationBarTransparentCompat
import org.xtimms.ridebus.widget.RideBusFullscreenDialog
import uy.kohesive.injekt.injectLazy

class MapStopsDialog :
    DialogController() {

    private val db: RideBusDatabase by injectLazy()
    private val preferences: PreferencesHelper by injectLazy()

    private var binding: MapStopsDialogBinding? = null

    private val points = mutableListOf<Point>()

    override fun onCreateDialog(savedViewState: Bundle?): Dialog {
        binding = MapStopsDialogBinding.inflate(checkNotNull(activity).layoutInflater)

        MapKitFactory.initialize(checkNotNull(activity))

        val cityPoint = db.cityDao().getCityCoordinates(preferences.city().get().toInt()).map {
            Point(
                it.latitude,
                it.longitude
            )
        }.first()

        for (coordinate in db.stopDao().getCoordinates(preferences.city().get().toInt())) {
            points.add(
                Point(
                    coordinate.latitude,
                    coordinate.longitude
                )
            )
        }

        binding?.toolbar?.apply {
            setNavigationOnClickListener { dialog?.dismiss() }
        }

        binding?.appbar?.applyInsetter {
            type(navigationBars = true, statusBars = true) {
                padding(left = true, top = true, right = true)
            }
        }

        binding?.mapView?.apply {
            applyInsetter {
                type(navigationBars = true) {
                    padding(bottom = true)
                }
            }
            map?.move(
                CameraPosition(
                    cityPoint,
                    14f,
                    0f,
                    0f
                )
            )
            map?.mapObjects?.addCollection()?.addPlacemarks(
                points,
                ImageProvider.fromResource(checkNotNull(activity), R.drawable.ic_stop_bitmap),
                IconStyle()
            )
        }

        binding?.mapView?.map?.isNightModeEnabled = checkNotNull(activity).isNightMode()

        return RideBusFullscreenDialog(checkNotNull(activity), checkNotNull(binding).root).apply {
            val typedValue = TypedValue()
            val theme = context.theme
            theme.resolveAttribute(android.R.attr.colorBackground, typedValue, true)
            window?.setBackgroundDrawable(
                ColorDrawable(
                    ColorUtils.setAlphaComponent(
                        typedValue.data,
                        230
                    )
                )
            )
        }
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        MapKitFactory.getInstance().onStart()
        binding?.mapView?.onStart()
        dialog?.window?.let { window ->
            window.setNavigationBarTransparentCompat(window.context)
            WindowCompat.setDecorFitsSystemWindows(window, false)
        }
    }

    override fun onDetach(view: View) {
        MapKitFactory.getInstance().onStop()
        binding?.mapView?.onStop()
        super.onDetach(view)
    }
}
