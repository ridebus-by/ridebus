package org.xtimms.ridebus.ui.details.stop

import android.os.Bundle
import android.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import dev.chrisbanes.insetter.applyInsetter
import eu.davidea.flexibleadapter.FlexibleAdapter
import org.xtimms.ridebus.R
import org.xtimms.ridebus.data.database.RideBusDatabase
import org.xtimms.ridebus.data.database.entity.Stop
import org.xtimms.ridebus.databinding.StopsRouteControllerBinding
import org.xtimms.ridebus.ui.base.controller.NoToolbarElevationController
import org.xtimms.ridebus.ui.base.controller.NucleusController
import org.xtimms.ridebus.ui.base.controller.withFadeTransaction
import org.xtimms.ridebus.ui.stub.StubController
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get
import uy.kohesive.injekt.injectLazy
import java.text.SimpleDateFormat
import java.util.*

class StopsOnRouteController :
    NucleusController<StopsRouteControllerBinding, StopsOnRoutePresenter>,
    NoToolbarElevationController,
    FlexibleAdapter.OnItemClickListener,
    FlexibleAdapter.OnUpdateListener,
    StopsOnRouteAdapter.OnItemClickListener {

    private val db: RideBusDatabase by injectLazy()

    constructor(stop: Stop?) : super(
        Bundle().apply {
            putInt(STOP_EXTRA, stop?.stopId ?: 0)
        }
    ) {
        this.stop = stop
    }

    constructor(stopId: Int) : this(Injekt.get<RideBusDatabase>().stopDao().getStop(stopId).firstOrNull())

    @Suppress("unused")
    constructor(bundle: Bundle) : this(bundle.getInt(STOP_EXTRA))

    var stop: Stop? = null
        private set

    init {
        setHasOptionsMenu(true)
    }

    /**
     * Adapter containing a list of routes on stop.
     */
    private var adapter: StopsOnRouteAdapter? = null

    override fun getTitle(): String? {
        return "${stop?.name}"
    }

    override fun getSubtitle(): String? {
        return "${stop?.direction}"
    }

    override fun createBinding(inflater: LayoutInflater) = StopsRouteControllerBinding.inflate(inflater)

    override fun createPresenter(): StopsOnRoutePresenter {
        return StopsOnRoutePresenter(stop!!)
    }

    override fun onViewCreated(view: View) {
        super.onViewCreated(view)

        binding.recycler.applyInsetter {
            type(navigationBars = true) {
                padding()
            }
        }

        val calendar: Calendar = Calendar.getInstance()
        val date: Date = calendar.getTime()
        binding.dayOfWeek.text = SimpleDateFormat("EEEE", Locale.getDefault()).format(date.time)

        // Init RecyclerView and adapter
        adapter = StopsOnRouteAdapter(this)
        binding.recycler.adapter = adapter
        binding.recycler.layoutManager = LinearLayoutManager(view.context)
        binding.recycler.setHasFixedSize(true)
    }

    override fun onDestroyView(view: View) {
        adapter = null
        super.onDestroyView(view)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.stops_on_route, menu)
    }

    override fun onUpdateEmptyView(size: Int) {
        if (size > 0) {
            binding.emptyView.hide()
        } else {
            binding.emptyView.show(R.drawable.ic_alert, R.string.information_no_route_on_stop)
        }
    }

    /**
     * Update view with stops
     *
     * @param stop stop object containing information about stop.
     */
    fun onNextStop(stop: Stop) {
        val items = db.routesAndStopsDao().getRoutesByStop(stop.stopId).map { StopsOnRouteItem(it) }
        adapter?.updateDataSet(items)
    }

    override fun onItemClick(view: View?, position: Int): Boolean {
        onItemClick(position)
        return false
    }

    override fun onItemClick(position: Int) {
        router.pushController(StubController().withFadeTransaction())
    }

    companion object {
        const val STOP_EXTRA = "stop"
    }
}
