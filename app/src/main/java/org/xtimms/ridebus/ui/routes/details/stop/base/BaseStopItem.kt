package org.xtimms.ridebus.ui.routes.details.stop.base

import eu.davidea.flexibleadapter.items.AbstractHeaderItem
import eu.davidea.flexibleadapter.items.AbstractSectionableItem
import org.xtimms.ridebus.data.database.entity.Stop

abstract class BaseStopItem<T : BaseStopHolder, H : AbstractHeaderItem<*>>(
    val stop: Stop,
    header: H? = null
) :
    AbstractSectionableItem<T, H?>(header) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other is BaseStopItem<*, *>) {
            return stop.stopId!! == other.stop.stopId!!
        }
        return false
    }

    override fun hashCode(): Int {
        return stop.stopId!!.hashCode()
    }
}
