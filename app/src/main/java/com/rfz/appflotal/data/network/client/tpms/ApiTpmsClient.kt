package com.rfz.appflotal.data.network.client.tpms

import com.rfz.appflotal.data.model.tpms.ConfigurationByIdMonitorResponse
import com.rfz.appflotal.data.model.tpms.CrudDiagramMonitorRequest
import com.rfz.appflotal.data.model.tpms.CrudMonitor
import com.rfz.appflotal.data.model.tpms.DiagramMonitorResponse
import com.rfz.appflotal.data.model.tpms.GetConfigurationsResponse
import com.rfz.appflotal.data.model.tpms.MonitorAlertsHistoryResponse
import com.rfz.appflotal.data.model.tpms.MonitorTireByDateResponse
import com.rfz.appflotal.data.model.tpms.PositionCoordinatesResponse
import com.rfz.appflotal.data.model.tpms.SensorRequest
import com.rfz.appflotal.data.model.tpms.TpmsResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiTpmsClient {
    @POST("api/Tpms/MonitorData")
    suspend fun sendSensorData(
        @Header("Authorization") token: String,
        @Body request: SensorRequest,
    ): Response<List<TpmsResponse>>

    @POST("api/Tpms/CrudDiagramMonitor")
    suspend fun postCrudDiagramMonitor(
        @Body request: CrudDiagramMonitorRequest
    )

    @POST("api/Tpms/CrudMonitor")
    suspend fun postCrudMonitor(
        @Header("Authorization") token: String,
        @Body request: CrudMonitor
    ): Response<List<TpmsResponse>?>

    @GET("api/Tpms/MonitorTireByDate")
    suspend fun getMonitorTireByDate(
        @Header("Authorization") token: String,
        @Query("id_monitor") monitorId: Int,
        @Query("fld_date") fldDate: String,
        @Query("position") position: String
    ): Response<List<MonitorTireByDateResponse>?>

    @GET("api/Tpms/MonitorAlertsHistory")
    suspend fun getMonitorAlertsHistory(
        @Query("id_monitor") idMonitor: Int,
    ): Response<List<MonitorAlertsHistoryResponse>>

    @GET("api/Tpms/DiagramMonitor")
    suspend fun getDiagramMonitor(
        @Header("Authorization") token: String,
        @Query("id_monitor") idMonitor: Int
    ): Response<List<DiagramMonitorResponse>>

    @GET("api/Tpms/Configuration")
    suspend fun getConfigurations(@Header("Authorization") token: String): Response<List<GetConfigurationsResponse>>

    @GET("api/Tpms/ConfigurationByIdMonitor")
    suspend fun getConfigurationByIdMonitor(
        @Header("Authorization") token: String,
        @Query("id_monitor") idMonitor: Int
    ): Response<List<ConfigurationByIdMonitorResponse>>

    @GET("api/show_claims")
    suspend fun checkLogin(): Response<Unit>

    @GET("api/Tpms/getPositionCoordinates")
    suspend fun getPositionCoordinates(
        @Header("Authorization") token: String,
        @Query("id_monitor") idMonitor: Int
    ): Response<List<PositionCoordinatesResponse>>
}