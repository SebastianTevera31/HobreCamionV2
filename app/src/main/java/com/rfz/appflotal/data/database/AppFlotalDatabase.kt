package com.rfz.appflotal.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.rfz.appflotal.data.dao.AppFlotalDao
import com.rfz.appflotal.data.model.flotalSoft.AppFlotalEntity

@Database(entities = arrayOf(AppFlotalEntity::class), version=1,exportSchema = false)
abstract class AppFlotalDatabase : RoomDatabase() {
    //DAO

    abstract fun flotalSoftDao(): AppFlotalDao

}