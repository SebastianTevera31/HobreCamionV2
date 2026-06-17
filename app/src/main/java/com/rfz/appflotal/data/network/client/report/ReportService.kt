package com.rfz.appflotal.data.network.client.report

import com.rfz.appflotal.data.model.report.CO2EmissionsReportResponse
import com.rfz.appflotal.data.model.report.CpkReportResponse
import com.rfz.appflotal.data.model.report.FuelConsumptionReportResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface ReportService {
    @POST("api/Reports/CPKByIDUser")
    suspend fun getCpkReportByIDUser(
        @Header("Authorization") token: String,
        @Query("id_user") idUser: Int,
    ): Response<List<CpkReportResponse>>

    @GET("api/Reports/getC02Emissions")
    suspend fun getCO2EmissionsReport(@Header("Authorization") token: String): Response<List<CO2EmissionsReportResponse>>

    @GET("api/Reports/FuelConsumption")
    suspend fun getFuelConsumptionReport(@Header("Authorization") token: String): Response<List<FuelConsumptionReportResponse>>
}