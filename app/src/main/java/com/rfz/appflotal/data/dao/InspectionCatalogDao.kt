package com.rfz.appflotal.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.rfz.appflotal.data.model.database.InspectionCatalogEntity

@Dao
interface InspectionCatalogDao {
    @Query("SELECT * FROM inspection_catalog WHERE isActive = 1")
    suspend fun getActiveCatalog(): List<InspectionCatalogEntity>

    @Upsert
    suspend fun upsertCatalog(items: List<InspectionCatalogEntity>)

    @Query("DELETE FROM inspection_catalog")
    suspend fun clearCatalog()
}