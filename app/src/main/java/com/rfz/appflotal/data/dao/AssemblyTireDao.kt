package com.rfz.appflotal.data.dao

import androidx.room.Upsert
import com.rfz.appflotal.data.model.assembly.AssemblyTireEntity

interface AssemblyTireDao {
    @Upsert
    suspend fun upsertAssemblyTire(assemblyTire: AssemblyTireEntity)
}