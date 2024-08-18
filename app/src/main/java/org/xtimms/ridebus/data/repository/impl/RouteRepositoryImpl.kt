package org.xtimms.ridebus.data.repository.impl

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Filter
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import org.xtimms.ridebus.data.model.Route
import org.xtimms.ridebus.data.repository.RouteRepository
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class RouteRepositoryImpl(
    private val routesRef: CollectionReference = Injekt.get()
) : RouteRepository {

    override fun getRoutes(transportId: Int, cityId: Int) = callbackFlow {
        val snapshotListener = routesRef
            .document("routes")
            .collection("polotsk") // TODO choose city
            .where(Filter.equalTo("transportId", transportId))
            .where(Filter.equalTo("cityId", cityId))
            .orderBy("number")
            .addSnapshotListener { snapshot, e ->
                val routesResponse = snapshot!!.toObjects(Route::class.java)
                trySend(routesResponse)
            }
        awaitClose {
            snapshotListener.remove()
        }
    }

    override fun getRoute(id: Int): Route {
        var route: Route? = null
        routesRef.document("routes")
            .collection("polotsk") // TODO choose city
            .document("route-$id")
            .get().addOnSuccessListener { document ->
                if (document != null) {
                    route = document.toObject(Route::class.java)
                }
            }
        return route!!
    }
}
