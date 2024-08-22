package org.xtimms.ridebus.data.repository.impl

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.getField
import com.google.firebase.firestore.ktx.getField
import kotlinx.coroutines.tasks.await
import org.xtimms.ridebus.data.model.City
import org.xtimms.ridebus.data.model.Coordinates
import org.xtimms.ridebus.data.repository.CityRepository
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class CityRepositoryImpl(
    private val ref: CollectionReference = Injekt.get()
) : CityRepository {

    override suspend fun getCities(): List<City> {
        return ref.get().await().toObjects(City::class.java)
    }

    override suspend fun getCitiesIds(): List<Int> {
        return ref.get().await().documents.mapNotNull { it.getLong("cityId")?.toInt() }
    }

    override suspend fun getCitiesNames(): List<String> {
        return ref.get().await().documents.mapNotNull { it.getString("city") }
    }

    override suspend fun getCityCoordinates(cityId: Int): List<Coordinates> {
        return try {
            val cityDoc = ref.document("city-$cityId").get().await()
            if (cityDoc.exists()) {
                val latitude = cityDoc.getString("latitude")
                val longitude = cityDoc.getString("longitude")
                listOf(Coordinates(latitude!!, longitude!!))
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            // Handle exceptions (e.g., document not found)
            emptyList()
        }
    }
}
