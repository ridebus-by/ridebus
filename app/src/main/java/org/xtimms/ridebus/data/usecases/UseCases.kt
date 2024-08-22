package org.xtimms.ridebus.data.usecases

data class UseCases(
    val getRoutes: GetRoutes,
    val getRoute: GetRoute,
    val getStops: GetStops,
    val getStop: GetStop,
    val getTransportTypesPerCity: GetTransportTypesPerCity,
    val getCities: GetCities,
    val getCitiesIds: GetCitiesIds,
    val getCitiesNames: GetCitiesNames,
    val getCityCoordinates: GetCityCoordinates
)
