package com.rfz.appflotal.data.database

import androidx.annotation.Keep
import androidx.room.Database
import androidx.room.RoomDatabase
import com.rfz.appflotal.data.dao.AppHCDao
import com.rfz.appflotal.data.dao.CoordinatesDao
import com.rfz.appflotal.data.dao.DataframeDao
import com.rfz.appflotal.data.dao.SensorDataDao
import com.rfz.appflotal.data.model.database.AppHCEntity
import com.rfz.appflotal.data.model.database.CoordinatesEntity
import com.rfz.appflotal.data.model.database.DataframeEntity
import com.rfz.appflotal.data.model.database.SensorDataEntity

@Keep
@Database(
    entities = [AppHCEntity::class, DataframeEntity::class, CoordinatesEntity::class, SensorDataEntity::class],
    version = 14,
    exportSchema = false
)
abstract class AppHombreCamionDatabase : RoomDatabase() {
    //DAO
    abstract fun hcSoftDao(): AppHCDao
    abstract fun dataframeDao(): DataframeDao
    abstract fun coordinatesDao(): CoordinatesDao
    abstract fun sensorDataDao(): SensorDataDao
}