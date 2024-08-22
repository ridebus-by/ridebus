package org.xtimms.ridebus.data.usecases

import org.xtimms.ridebus.data.repository.CityRepository

class GetCities(
    private val repo: CityRepository
) {
    suspend operator fun invoke() = repo.getCities()
}

class GetCitiesIds(
    private val repo: CityRepository
) {
    suspend operator fun invoke() = repo.getCitiesIds()
}

class GetCitiesNames(
    private val repo: CityRepository
) {
    suspend operator fun invoke() = repo.getCitiesNames()
}

class GetCityCoordinates(
    private val repo: CityRepository
) {
    suspend operator fun invoke(cityId: Int) = repo.getCityCoordinates(cityId)
}
