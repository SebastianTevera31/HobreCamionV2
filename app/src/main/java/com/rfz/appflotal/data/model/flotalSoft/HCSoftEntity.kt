package com.rfz.appflotal.data.model.flotalSoft

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AppHCEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val id_user: Int,
    val fld_name: String,
    val fld_email: String,
    val fld_token: String,
    val fecha: String
)

@Entity(tableName = "sensorTpms")
data class SensorTpmsEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_record")
    val idRecord: Long = 0L,
    @ColumnInfo(name = "monitor_id")
    val monitorId: Int,
    @ColumnInfo(name = "sensor_id")
    val sensorId: String,
    @ColumnInfo(name = "data_frame")
    val dataFrame: String,
    @ColumnInfo(name = "timestamp")
    val timestamp: String,
    @ColumnInfo(name = "sendStatus")
    val sent: Boolean
)