package com.rfz.appflotal.data.model.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class AppHCEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val idUser: Int,
    val fld_name: String,
    val fld_username: String,
    val fld_email: String,
    val fld_token: String,
    val id_monitor: Int = 0,
    val country: Int,
    val industry: Int,
    val vehicleType: String,
    val monitorMac: String,
    val baseConfiguration: String,
    val idVehicle: Int,
    val vehiclePlates: String,
    val paymentPlan: String,
    val fecha: String,
    val termsGranted: Boolean,
    val odometer: Int = 0,
    val dateLastOdometer: String = "",
)

@Entity(tableName = "dataframe")
data class DataframeEntity(
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
    val sent: Boolean,
    @ColumnInfo(name = "active")
    val active: Boolean
)

@Entity(tableName = "coordinates")
data class CoordinatesEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,
    @ColumnInfo(name = "monitor_id")
    val monitorId: Int,
    @ColumnInfo(name = "idPosition")
    val idPosition: String,
    @ColumnInfo(name = "isAssembled")
    val isAssembled: Boolean,
    @ColumnInfo(name = "inAlert")
    val inAlert: Boolean,
    @ColumnInfo(name = "inActive")
    val isActive: Boolean,
    @ColumnInfo(name = "xPosition")
    val xPosition: Int,
    @ColumnInfo(name = "yPosition")
    val yPosition: Int,
)

@Entity(tableName = "sensor_data")
data class SensorDataEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "monitor_id")
    val idMonitor: Int,
    @ColumnInfo(name = "tire")
    val tire: String,
    @ColumnInfo(name = "tire_number")
    val tireNumber: String,
    @ColumnInfo(name = "timestamp")
    val timestamp: String,
    @ColumnInfo(name = "last_inspection")
    val lastInspection: String? = null,
    @ColumnInfo(name = "temperature")
    val temperature: Int,
    @ColumnInfo(name = "pressure")
    val pressure: Int,
    @ColumnInfo(name = "high_temperature_alert")
    val highTemperatureAlert: Boolean,
    @ColumnInfo(name = "high_pressure_alert")
    val highPressureAlert: Boolean,
    @ColumnInfo(name = "low_pressure_alert")
    val lowPressureAlert: Boolean,
    @ColumnInfo(name = "low_battery_alert")
    val lowBatteryAlert: Boolean,
    @ColumnInfo(name = "puncture_alert")
    val punctureAlert: Boolean,
    @ColumnInfo(name = "active")
    val active: Boolean
)

data class SensorDataUpdate(
    val idMonitor: Int,
    val tire: String,
    val tireNumber: String,
    val timestamp: String,
    val temperature: Int,
    val pressure: Int,
    val highTemperatureAlert: Boolean,
    val highPressureAlert: Boolean,
    val lowPressureAlert: Boolean,
    val lowBatteryAlert: Boolean,
    val punctureAlert: Boolean,
    val active: Boolean
)
