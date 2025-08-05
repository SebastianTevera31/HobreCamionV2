package com.rfz.appflotal.data.network.client.tpms

import com.rfz.appflotal.data.model.tpms.ConfigurationByIdMonitorResponse
import com.rfz.appflotal.data.model.tpms.CrudDiagramMonitorRequest
import com.rfz.appflotal.data.model.tpms.DiagramMonitorResponse
import com.rfz.appflotal.data.model.tpms.MonitorAlertsHistoryResponse
import com.rfz.appflotal.data.model.tpms.MonitorDataByDateResponse
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
        @Body request: SensorRequest,
    ): Response<List<TpmsResponse>>

    @POST("api/DiagramMonitor/CrudDiagramMonitor")
    suspend fun postCrudDiagramMonitor(
        @Body request: CrudDiagramMonitorRequest
    )

    @GET("api/Tpms/MonitorDataByDate")
    suspend fun getMonitorDataByDate(

        @Query("id_monitor") idMonitor: Int,
        @Query("fld_date") fldDate: String
    ): Response<List<MonitorDataByDateResponse>>

    @GET("api/Tpms/MonitorAlertsHistory")
    suspend fun getMonitorAlertsHistory(
        @Query("id_monitor") idMonitor: Int,
    ): Response<List<MonitorAlertsHistoryResponse>>

    @GET("api/DiagramMonitor/DiagramMonitor")
    suspend fun getDiagramMonitor(
        @Header("Authorization") token: String,
        @Query("id_monitor") idMonitor: Int
    ): Response<List<DiagramMonitorResponse>>

    @GET("api/Configuration/ConfigurationByIdMonitor")
    suspend fun getConfigurationByIdMonitor(
        @Header("Authorization") token: String,
        @Query("id_monitor") idMonitor: Int
    ): Response<List<ConfigurationByIdMonitorResponse>>

    @GET("api/show_claims")
    suspend fun checkLogin(): Response<Unit>
}