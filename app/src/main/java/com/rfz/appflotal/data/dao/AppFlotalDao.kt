package com.rfz.appflotal.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.rfz.appflotal.data.model.flotalSoft.AppFlotalEntity

import kotlinx.coroutines.flow.Flow

@Dao
interface AppFlotalDao {

    @Query("SELECT * from AppFlotalEntity")
    fun getData(): Flow<List<AppFlotalEntity>>



    @Query("DELETE FROM AppFlotalEntity")
    suspend fun deleteAllFlotalSoft()


    @Delete
    suspend fun deleteFlotalSoft(item: AppFlotalEntity)



    @Insert
    suspend fun addFlotalSoft(item:AppFlotalEntity)





}