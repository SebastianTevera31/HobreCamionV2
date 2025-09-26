package com.rfz.appflotal.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.rfz.appflotal.data.model.database.CoordinatesEntity


@Dao
interface CoordinatesDao {
    @Insert
    suspend fun insertCoordinates(coords: CoordinatesEntity)
    @Query("SELECT * FROM coordinates WHERE monitor_id =:monitorId")
    suspend fun getCoordinates(monitorId: Int): List<CoordinatesEntity>

    @Query("DELETE FROM coordinates WHERE monitor_id =:monitorId")
    suspend fun deleteCoordinates(monitorId: Int)
}