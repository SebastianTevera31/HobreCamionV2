package com.rfz.appflotal.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.rfz.appflotal.data.model.flotalSoft.AppHCEntity

import kotlinx.coroutines.flow.Flow

@Dao
interface AppHCDao {
    @Query("SELECT * from AppHCEntity")
    fun getData(): Flow<List<AppHCEntity>>

    @Query("UPDATE AppHCEntity SET id_monitor =:idMonitor, monitorMac =:mac, baseConfiguration =:baseConfiguration WHERE id_user =:idUser")
    suspend fun updateMonitorId(idMonitor: Int, mac: String, baseConfiguration: String, idUser: Int)

    @Query("DELETE FROM AppHCEntity")
    suspend fun deleteAllFlotalSoft()

    @Delete
    suspend fun deleteFlotalSoft(item: AppHCEntity)

    @Insert
    suspend fun addFlotalSoft(item: AppHCEntity)
}