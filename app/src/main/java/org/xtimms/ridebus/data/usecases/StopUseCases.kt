package org.xtimms.ridebus.data.usecases

import org.xtimms.ridebus.data.repository.StopRepository

class GetStops(
    private val repo: StopRepository
) {
    operator fun invoke(cityId: Int) = repo.getStops(cityId)
}

class GetStop(
    private val repo: StopRepository
) {
    suspend operator fun invoke(stopId: Int) = repo.getStop(stopId)
}
