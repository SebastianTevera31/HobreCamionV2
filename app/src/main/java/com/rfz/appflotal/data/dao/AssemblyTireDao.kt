package com.rfz.appflotal.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.rfz.appflotal.data.model.assembly.AssemblyTireEntity
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET

@Dao
interface AssemblyTireDao {
    @Query("SELECT * FROM assembly_tire_table")
    fun observeAssemblyTires(): Flow<List<AssemblyTireEntity>>

    @Query("SELECT * FROM assembly_tire_table WHERE positionTire =:position")
    suspend fun getAssemblyTire(position: String): AssemblyTireEntity

    @Upsert
    suspend fun upsertAssemblyTire(assemblyTire: AssemblyTireEntity)

}