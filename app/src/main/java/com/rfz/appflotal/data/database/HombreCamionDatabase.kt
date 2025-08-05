package com.rfz.appflotal.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.rfz.appflotal.data.dao.AppHCDao
import com.rfz.appflotal.data.dao.SensorDao
import com.rfz.appflotal.data.model.flotalSoft.AppHCEntity
import com.rfz.appflotal.data.model.flotalSoft.SensorTpmsEntity

@Database(
    entities = [AppHCEntity::class, SensorTpmsEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AppHombreCamionDatabase : RoomDatabase() {
    //DAO
    abstract fun hcSoftDao(): AppHCDao
    abstract fun sensorDao(): SensorDao
}