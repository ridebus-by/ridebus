package org.xtimms.ridebus.data.database.dao

import androidx.room.Dao
import androidx.room.Query

@Dao
interface TransportDao {

    @Query("SELECT _id FROM transport WHERE EXISTS (SELECT * FROM route WHERE transport._id = route.transport_id AND city_id = :cityId)")
    fun getTypesOfTransportPerCity(cityId: Int): List<Int>
}
