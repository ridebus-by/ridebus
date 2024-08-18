package org.xtimms.ridebus.data.usecases

import org.xtimms.ridebus.data.repository.StopRepository

class GetStops(
    private val repo: StopRepository
) {
    operator fun invoke(cityId: Int) = repo.getStops(cityId)
}
