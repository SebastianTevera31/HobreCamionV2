package com.rfz.appflotal.data.network.client.report

import com.rfz.appflotal.data.model.report.CO2EmissionsReportResponse
import com.rfz.appflotal.data.model.report.CpkReportRequest
import com.rfz.appflotal.data.model.report.CpkReportResponse
import com.rfz.appflotal.data.model.report.FuelConsumptionReportResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ReportService {

    @POST("api/Reports/CPK")
    suspend fun getCpkReport(
        @Header("Authorization") token: String,
        @Body request: CpkReportRequest
    ): Response<List<CpkReportResponse>>

    @POST("api/Reports/CPKByIDUser")
    suspend fun getCpkReportByIDUser(
        @Header("Authorization") token: String,
        @Body request: CpkReportRequest
    ): Response<List<CpkReportResponse>>

    @GET("api/Reports/getC02Emissions")
    suspend fun getCO2EmissionsReport(@Header("Authorization") token: String): Response<List<CO2EmissionsReportResponse>>

    @GET("api/Reports/FuelConsumption")
    suspend fun getFuelConsumptionReport(@Header("Authorization") token: String): Response<List<FuelConsumptionReportResponse>>
}