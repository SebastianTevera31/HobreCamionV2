package com.rfz.appflotal.data.network.client.alerts

import com.rfz.appflotal.data.model.alerts.AlertDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface AlertsService {

    @GET("/api/Tpms/Alerts")
    suspend fun getAlerts(
        @Header("Authorization") token: String,
        @Query("fechaInicial") startDate: String,
        @Query("fechaFinal") endDate: String,
        @Query("position") position: String,
        @Query("tipoAlerta") alertType: String,
        @Query("start") startPaging: Int
    ): Response<List<AlertDto>>
}