package org.xtimms.ridebus.data.usecases

import org.xtimms.ridebus.data.repository.TransportRepository

class GetTransportTypesPerCity(
    private val repo: TransportRepository
) {
    suspend operator fun invoke(cityId: Int) = repo.getTypesOfTransportPerCity(cityId)
}
