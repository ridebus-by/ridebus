package org.xtimms.ridebus.data.repository.impl

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Filter
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import logcat.LogPriority
import org.xtimms.ridebus.data.model.Route
import org.xtimms.ridebus.data.repository.RouteRepository
import org.xtimms.ridebus.util.system.logcat
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class RouteRepositoryImpl(
    private val ref: CollectionReference = Injekt.get()
) : RouteRepository {

    override fun getRoutes(transportId: Int, cityId: Int) = callbackFlow {
        val snapshotListener = ref
            .document("routes")
            .collection("polotsk") // TODO choose city
            .where(Filter.equalTo("transportId", transportId))
            .where(Filter.equalTo("cityId", cityId))
            .orderBy("number")
            .addSnapshotListener { snapshot, e ->
                val routesResponse = snapshot?.toObjects(Route::class.java) ?: emptyList()
                trySend(routesResponse)
            }
        awaitClose {
            snapshotListener.remove()
        }
    }

    override suspend fun getRoute(id: Int): Route? {
        return try {
            val snapshot = ref.document("routes")
                .collection("polotsk") // TODO choose city
                .document("route-$id")
                .get()
                .await()
            snapshot.toObject(Route::class.java)
        } catch (e: Exception) {
            logcat(LogPriority.ERROR, e) { "Error getting route $id: ${e.message}" }
            null
        }
    }
}
