package org.xtimms.ridebus.data.usecases

import org.xtimms.ridebus.data.repository.RouteRepository

class GetRoute(
    private val repo: RouteRepository
) {
    operator fun invoke(routeId: Int) = repo.getRoute(routeId)
}
