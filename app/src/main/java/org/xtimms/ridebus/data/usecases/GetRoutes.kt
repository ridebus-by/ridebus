package org.xtimms.ridebus.data.usecases

import org.xtimms.ridebus.data.repository.RouteRepository

class GetRoutes(
    private val repo: RouteRepository
) {
    operator fun invoke(transportId: Int, cityId: Int) = repo.getRoutes(transportId, cityId)
}
