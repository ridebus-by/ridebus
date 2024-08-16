package org.xtimms.ridebus.data.repository.impl

import com.google.firebase.firestore.CollectionReference
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import org.xtimms.ridebus.data.model.Route
import org.xtimms.ridebus.data.repository.RouteRepository
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get
import javax.inject.Singleton

@Singleton
class RouteRepositoryImpl(
    private val routesRef: CollectionReference = Injekt.get()
) : RouteRepository {

    override fun getRoutes() = callbackFlow {
        val snapshotListener = routesRef.orderBy("number").addSnapshotListener { snapshot, e ->
            val routesResponse = snapshot!!.toObjects(Route::class.java)
            trySend(routesResponse)
        }
        awaitClose {
            snapshotListener.remove()
        }
    }

    override fun getRoute(id: Int): Route {
        var route: Route? = null
        routesRef.document("route-$id").get().addOnSuccessListener { document ->
            route = document?.toObject(Route::class.java)
        }
        return route!!
    }
}
