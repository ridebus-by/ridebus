package org.xtimms.ridebus.ui.routes.details.stop

import org.xtimms.ridebus.ui.routes.details.RouteDetailsController
import org.xtimms.ridebus.ui.routes.details.stop.base.BaseStopsAdapter

class StopsOnRouteAdapter(
    controller: RouteDetailsController
) : BaseStopsAdapter<StopOnRouteItem>(controller) {

    var items: List<StopOnRouteItem> = emptyList()

    override fun updateDataSet(items: List<StopOnRouteItem>?) {
        this.items = items ?: emptyList()
        super.updateDataSet(items)
    }

    fun indexOf(item: StopOnRouteItem): Int {
        return items.indexOf(item)
    }
}
