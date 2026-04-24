package com.rfz.appflotal.data.model.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.joda.time.DateTime
import java.sql.Date

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

@Entity(tableName = "disassembly_tire_table")
data class DisassemblyTireEntity(
    @PrimaryKey
    val positionTire: String,
    val disassemblyCause: Int,
    val destination: Int,
    val dateOperation: String,
    val odometer: Int,
    val updatedAt: Long
)

@Entity(tableName = "inspection_catalog")
data class InspectionCatalogEntity(
    @PrimaryKey val idTireInspectionReport: Int,
    val description: String,
    val isActive: Boolean = true
)

@Entity(tableName = "inspection_tire_table")
data class InspectionTireEntity(
    @PrimaryKey
    val positionTire: String,
    val treadDepth: Float,
    val treadDepth2: Float,
    val treadDepth3: Float,
    val treadDepth4: Float,
    val tireInspectionReportId: Int,
    val pressureInspected: Int,
    val dateInspection: String,
    val odometer: Int,
    val temperatureInspected: Int,
    val pressureAdjusted: Int,
    val updatedAt: Long
)

@Entity(tableName = "tire")
data class TireEntity(
    @PrimaryKey
    val id_tire: Int,
    val fld_provider: String,
    val fld_size: String,
    val fld_brand: String,
    val fld_model: String,
    val fld_loadingCapacity: String,
    val fld_destination: String,
    val fld_typeAcquisition: String,
    val fld_lastMountedPosition: String,
    val fld_descriptionLastRenovatedDesign: String,
    val fld_lastMountedPositionDate: String,
    val fld_vehicleNumber: Int,
    val fld_dateEventAssembly: Date,
    val fld_dateEventA: DateTime,
    val fld_treadDepthAssembly: Double,
    val fld_odometerAssembly: Int,
    val fld_typeVehicle: String,
    val axleID: Int,
    val tirePosition: Int
)