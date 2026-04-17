package com.rfz.appflotal.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.rfz.appflotal.data.model.database.InspectionTireEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InspectionTireDao {
    @Query("SELECT * FROM inspection_tire_table")
    fun observeInspectionTires(): Flow<List<InspectionTireEntity>>

    @Query("SELECT * FROM inspection_tire_table WHERE positionTire =:position")
    suspend fun getInspectionTire(position: String): InspectionTireEntity?

    @Upsert
    suspend fun upsertInspectionTire(inspectionTire: InspectionTireEntity)

    @Query("DELETE FROM inspection_tire_table WHERE positionTire =:position")
    suspend fun deleteInspectionTire(position: String)
}