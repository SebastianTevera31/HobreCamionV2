package com.rfz.appflotal.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.rfz.appflotal.data.model.assembly.AssemblyTireEntity

@Dao
interface AssemblyTireDao {

    @Query("SELECT * FROM assembly_tire_table WHERE positionTire =:position")
    suspend fun getAssemblyTire(position: String): AssemblyTireEntity

    @Upsert
    suspend fun upsertAssemblyTire(assemblyTire: AssemblyTireEntity)

}