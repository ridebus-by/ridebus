package org.xtimms.ridebus.data.repository.impl

import com.google.firebase.firestore.CollectionReference
import kotlinx.coroutines.tasks.await
import org.xtimms.ridebus.data.repository.TransportRepository
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class TransportRepositoryImpl(
    private val ref: CollectionReference = Injekt.get()
) : TransportRepository {

    override suspend fun getTypesOfTransportPerCity(cityId: Int): List<Int> {
        val transportIds = ref
            .document("transportTypes")
            .collection("types")
            .whereEqualTo("cityId", cityId)
            .orderBy("transportId")
            .get()
            .await()
            .documents
            .mapNotNull { it.getLong("transportId")?.toInt() } // Get transport IDs

        return transportIds.distinct() // Get distinct transport types
    }
}
