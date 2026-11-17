package com.rfz.appflotal.data.network.client.fueling

import com.rfz.appflotal.data.model.fueling.FuelingHistoryResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface FuelingService {

    @Multipart
    @POST("api/Vehicle/insertFueling")
    suspend fun insertFueling(
        @Header("Authorization") token: String,
        @Query("fuelType") fuelType: Int,
        @Query("fuelUnits") fuelUnits: Double,
        @Query("isLiter") isLiter: Boolean,
        @Query("amount") amount: Double,
        @Query("odometer") odometer: Int,
        @Query("fuelingDate") fuelingDate: String,
        @Query("station") station: String,
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("fld_percentage") percentage: Int,
        @Part fld_imageTicket: MultipartBody.Part
    ): Response<Unit>

    /** Los filtros nulos no se envían. [start] es la página (1 = primera). */
    @GET("api/Vehicle/getFuelingHistory")
    suspend fun getFuelingHistory(
        @Header("Authorization") token: String,
        @Query("fecha_i") startDate: String? = null,
        @Query("fecha_f") endDate: String? = null,
        @Query("tipoCombustible") fuelType: Int? = null,
        @Query("start") start: Int = 1,
        @Query("pageSize") pageSize: Int = 10
    ): Response<FuelingHistoryResponse>
}
