package org.xtimms.ridebus.data.repository.impl

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Filter
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import org.xtimms.ridebus.data.model.Stop
import org.xtimms.ridebus.data.repository.StopRepository
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class StopRepositoryImpl(
    private val stopsRef: CollectionReference = Injekt.get()
) : StopRepository {

    override fun getStops(cityId: Int) = callbackFlow {
        val snapshotListener = stopsRef
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
}
