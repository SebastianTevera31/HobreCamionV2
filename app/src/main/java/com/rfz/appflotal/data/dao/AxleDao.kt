package com.rfz.appflotal.data.dao

import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.rfz.appflotal.data.model.axle.AxleEntity

interface AxleDao {
    @Upsert
    suspend fun upsertAxle(axle: AxleEntity)

    @Query("SELECT * FROM axle WHERE id_axle = :idAxle")
    suspend fun getAxleById(idAxle: Int): AxleEntity

    @Delete
    suspend fun deleteAxle(axle: AxleEntity)
}

