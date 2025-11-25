package com.rfz.appflotal.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.rfz.appflotal.data.model.axle.AxleEntity

@Dao
interface AxleDao {
    @Query("SELECT * FROM axle_table")
    suspend fun getAxle(): List<AxleEntity>

    @Upsert
    suspend fun upsertAxles(axle: List<AxleEntity>)
}

