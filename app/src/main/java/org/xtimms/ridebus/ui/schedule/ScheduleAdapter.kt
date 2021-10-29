package org.xtimms.ridebus.ui.schedule

import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.IFlexible

class ScheduleAdapter(controller: ScheduleController) :
    FlexibleAdapter<IFlexible<*>>(null, controller, true)
