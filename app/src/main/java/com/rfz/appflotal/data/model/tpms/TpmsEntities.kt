package com.rfz.appflotal.data.model.tpms

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import retrofit2.http.Header

// api/Tpms/SensorData
data class SensorRequest(
    @Header("Authorization") val token: String,
    @SerialName("fld_frame")
    val fldFrame: String,
    @SerialName("id_monitor_fk_1")
    val monitor: Int,
    @SerialName("fld_dateData")
    val fldDateData: String
)

data class CrudDiagramMonitorRequest(
    @SerialName("id_diagramMonitor") val idDiagramMonitor: Int,
    @SerialName("monitor_id") val monitorId: Int,
    @SerialName("axle_id") val axleId: Int,
    @SerialName("position_id") val positionId: Int,
    @SerialName("sensor_id") val sensorId: Int,
)

data class TpmsResponse(val id: Int, val message: String)

data class MonitorDataByDateResponse(
    @SerialName("fld_lowBits") val fldLowBits: String,
    @SerialName("fld_mac") val fldMac: String,
    @SerialName("fld_dateData") val fldDateData: String,
    @SerialName("fld_temperature") val fldTemperature: Float,
    @SerialName("fld_sensorId") val fldSensorId: String,
    @SerialName("fld_position") val fldPosition: String,
    @SerialName("fld_psi") val fldPsi: Float
)

data class MonitorAlertsHistoryResponse(
    @SerialName("fld_mac") val fldMac: String,
    @SerialName("fld_dateData") val fldDateData: String,
    @SerialName("fld_sensorId") val fldSensorId: String,
    @SerialName("fld_position") val fldPosition: String,
    @SerialName("fld_highTemperature") val fldHighTemperature: Int,
    @SerialName("fld_lowPressure") val fldLowPressure: Int,
    @SerialName("fld_highPressure") val fldHighPressure: Int,
    @SerialName("fld_nodataReceivedIn60min") val fldNoData: Int,
    @SerialName("fld_lowSensorBattery") val fldLowSensorBattery: Int,
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
    @SerializedName("sensor_position") val sensorPosition: Int,
    @SerializedName("position_description") val positionDescription: String,
    @SerializedName("sensor_id") val sensorId: Int,
    @SerializedName("sensor_name") val sensorName: String,
    @SerializedName("configuration_id") val configId: Int,
    @SerializedName("configuration_description") val configDescription: String,
    @SerializedName("psi") val psi: Float,
    @SerializedName("temperature") val temperature: Float,
    @SerializedName("highTemperature") val highTemperature: String,
    @SerializedName("lowPressure") val lowPressure: Boolean,
    @SerializedName("highPressure") val highPressure: Boolean,
    @SerializedName("ultimalectura") val ultimalectura: Boolean,
)