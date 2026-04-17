package com.rfz.appflotal.data.database

import androidx.annotation.Keep
import androidx.room.Database
import androidx.room.RoomDatabase
import com.rfz.appflotal.data.dao.AppHCDao
import com.rfz.appflotal.data.dao.AssemblyTireDao
import com.rfz.appflotal.data.dao.AxleDao
import com.rfz.appflotal.data.dao.CoordinatesDao
import com.rfz.appflotal.data.dao.DataframeDao
import com.rfz.appflotal.data.dao.SensorDataDao
import com.rfz.appflotal.data.dao.DisassemblyTireDao
import com.rfz.appflotal.data.dao.InspectionTireDao
import com.rfz.appflotal.data.model.assembly.AssemblyTireEntity
import com.rfz.appflotal.data.model.axle.AxleEntity
import com.rfz.appflotal.data.model.database.AppHCEntity
import com.rfz.appflotal.data.model.database.CoordinatesEntity
import com.rfz.appflotal.data.model.database.DataframeEntity
import com.rfz.appflotal.data.model.database.SensorDataEntity
import com.rfz.appflotal.data.model.disassembly.tire.DisassemblyTireEntity
import com.rfz.appflotal.data.model.tire.dto.InspectionTireEntity

@Keep
@Database(
    entities = [
        AppHCEntity::class, DataframeEntity::class, CoordinatesEntity::class,
        SensorDataEntity::class, AssemblyTireEntity::class, AxleEntity::class,
        DisassemblyTireEntity::class, InspectionTireEntity::class
    ],
    version = 24,
    exportSchema = false
)
abstract class AppHombreCamionDatabase : RoomDatabase() {
    //DAO
    abstract fun hcSoftDao(): AppHCDao
    abstract fun dataframeDao(): DataframeDao
    abstract fun coordinatesDao(): CoordinatesDao
    abstract fun sensorDataDao(): SensorDataDao

    abstract fun assemblyTireDao(): AssemblyTireDao
    abstract fun disassemblyTireDao(): DisassemblyTireDao
    abstract fun inspectionTireDao(): InspectionTireDao

    abstract fun axleDao(): AxleDao
}