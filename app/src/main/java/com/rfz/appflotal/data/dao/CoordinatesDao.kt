package com.rfz.appflotal.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.rfz.appflotal.data.model.database.CoordinatesEntity


@Dao
interface CoordinatesDao {
    @Insert
    suspend fun insertCoordinates(coords: List<CoordinatesEntity>)

    /**
     * Sustituye las coordenadas del monitor en una sola transacción. Borrar e insertar por
     * separado dejaba posiciones duplicadas cuando dos cargas del monitor se intercalaban
     * (borrar, borrar, insertar, insertar), y el LazyVerticalGrid del monitor truena con
     * keys repetidas.
     */
    @Transaction
    suspend fun replaceCoordinates(monitorId: Int, coords: List<CoordinatesEntity>) {
        deleteCoordinates(monitorId)
        insertCoordinates(coords)
    }

    @Query("SELECT * FROM coordinates WHERE monitor_id =:monitorId")
    suspend fun getCoordinates(monitorId: Int): List<CoordinatesEntity>

    @Query("DELETE FROM coordinates WHERE monitor_id =:monitorId")
    suspend fun deleteCoordinates(monitorId: Int)

    @Query("UPDATE coordinates SET inActive =:isActive, inAlert =:isAlert WHERE monitor_id =:monitorId AND idPosition =:tire")
    suspend fun updateCoordinates(monitorId: Int, tire: String, isActive: Boolean, isAlert: Boolean)

    @Query("UPDATE coordinates SET isAssembled = :isAssembled WHERE monitor_id = :monitorId AND idPosition = :tire")
    suspend fun updateAssemblyStatus(monitorId: Int, tire: String, isAssembled: Boolean)
}