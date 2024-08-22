package org.xtimms.ridebus.data.repository.impl

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Filter
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import logcat.LogPriority
import org.xtimms.ridebus.data.model.Stop
import org.xtimms.ridebus.data.repository.StopRepository
import org.xtimms.ridebus.util.system.logcat
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class StopRepositoryImpl(
    private val ref: CollectionReference = Injekt.get()
) : StopRepository {

    override fun getStops(cityId: Int) = callbackFlow {
        val snapshotListener = ref
            .document("stops")
            .collection("polotsk")
            .where(Filter.equalTo("cityId", cityId))
            .orderBy("name")
            .addSnapshotListener { snapshot, e ->
                val stopsResponse = snapshot!!.toObjects(Stop::class.java)
                trySend(stopsResponse)
            }
        awaitClose {
            snapshotListener.remove()
        }
    }

    override suspend fun getStop(stopId: Int): Stop? {
        return try {
            val snapshot = ref.document("stops")
                .collection("polotsk") // TODO choose city
                .document("stop-$stopId")
                .get()
                .await()
            snapshot.toObject(Stop::class.java)
        } catch (e: Exception) {
            logcat(LogPriority.ERROR, e) { "Error getting stop $stopId: ${e.message}" }
            null
        }
    }
}
