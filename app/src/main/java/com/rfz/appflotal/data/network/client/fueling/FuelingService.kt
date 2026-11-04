package com.rfz.appflotal.data.network.client.fueling

import com.rfz.appflotal.data.model.fueling.FuelingHistoryResponse
import com.rfz.appflotal.data.model.fueling.InsertFuelingDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface FuelingService {

    // El body de la respuesta no se usa: basta con el código HTTP para confirmar el alta.
    @POST("api/Vehicle/insertFueling")
    suspend fun insertFueling(
        @Header("Authorization") token: String,
        @Body request: InsertFuelingDto
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
