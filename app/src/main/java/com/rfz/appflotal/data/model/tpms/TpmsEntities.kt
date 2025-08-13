package com.rfz.appflotal.data.model.tpms

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName

// api/Tpms/SensorData
data class SensorRequest(
    @SerializedName("fld_frame") val fldFrame: String,
    @SerializedName("id_monitor_fk_1") val monitor: Int,
    @SerializedName("fld_dateData") val fldDateData: String
)

data class CrudDiagramMonitorRequest(
    @SerializedName("id_diagramMonitor") val idDiagramMonitor: Int,
    @SerializedName("monitor_id") val monitorId: Int,
    @SerializedName("axle_id") val axleId: Int,
    @SerializedName("position_id") val positionId: Int,
    @SerializedName("sensor_id") val sensorId: Int,
)

data class TpmsResponse(val id: Int, val message: String)

data class MonitorDataByDateResponse(
    @SerializedName("fld_lowBits") val fldLowBits: String,
    @SerializedName("fld_mac") val fldMac: String,
    @SerializedName("fld_dateData") val fldDateData: String,
    @SerializedName("fld_temperature") val fldTemperature: Float,
    @SerializedName("fld_sensorId") val fldSensorId: String,
    @SerializedName("fld_position") val fldPosition: String,
    @SerializedName("fld_psi") val fldPsi: Float
)

data class MonitorAlertsHistoryResponse(
    @SerializedName("fld_mac") val fldMac: String,
    @SerializedName("fld_dateData") val fldDateData: String,
    @SerializedName("fld_sensorId") val fldSensorId: String,
    @SerializedName("fld_position") val fldPosition: String,
    @SerializedName("fld_highTemperature") val fldHighTemperature: Int,
    @SerializedName("fld_lowPressure") val fldLowPressure: Int,
    @SerializedName("fld_highPressure") val fldHighPressure: Int,
    @SerializedName("fld_nodataReceivedIn60min") val fldNoData: Int,
    @SerializedName("fld_lowSensorBattery") val fldLowSensorBattery: Int,
)

data class ConfigurationByIdMonitorResponse(
    @SerializedName("id_configuration") val idConfiguration: Int,
    @SerializedName("fld_description") val fldDescription: String,
    @SerializedName("fld_UrlImage") val fldUrlImage: String,
    @SerializedName("fld_svg") val fldSvg: String,
)

data class DiagramMonitorResponse(
    @SerializedName("id_diagramMonitor") val idDiagramaMonitor: Int,
    @SerializedName("monitor_id") val monitorId: Int,
    @SerializedName("monitor_MAC") val monitorMac: String,
    @SerializedName("axle_id") val axleId: Int,
    @SerializedName("axle_description") val axleDescription: String,
    @SerializedName("sensor_position") val sensorPosition: String,
    @SerializedName("position_description") val positionDescription: String,
    @SerializedName("sensor_id") val sensorId: Int,
    @SerializedName("sensor_name") val sensorName: String,
    @SerializedName("configuration_id") val configId: Int,
    @SerializedName("configuration_description") val configDescription: String,
    @SerializedName("psi") val psi: Float,
    @SerializedName("tireNumber") val tireNumber: String = "",
    @SerializedName("temperature") val temperature: Float,
    @SerializedName("highTemperature") val highTemperature: Boolean,
    @SerializedName("lowPressure") val lowPressure: Boolean,
    @SerializedName("highPressure") val highPressure: Boolean,
    @SerializedName("ultimalectura") val ultimalectura: String,
)