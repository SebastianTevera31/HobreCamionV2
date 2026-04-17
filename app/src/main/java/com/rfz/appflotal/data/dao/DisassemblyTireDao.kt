package com.rfz.appflotal.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.rfz.appflotal.data.model.database.DisassemblyTireEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DisassemblyTireDao {
    @Query("SELECT * FROM disassembly_tire_table")
    fun observeDisassemblyTires(): Flow<List<DisassemblyTireEntity>>

    @Query("SELECT * FROM disassembly_tire_table WHERE positionTire =:position")
    suspend fun getDisassemblyTire(position: String): DisassemblyTireEntity?

    @Upsert
    suspend fun upsertDisassemblyTire(disassemblyTire: DisassemblyTireEntity)

    @Query("DELETE FROM disassembly_tire_table WHERE positionTire =:position")
    suspend fun deleteDisassemblyTire(position: String)
}