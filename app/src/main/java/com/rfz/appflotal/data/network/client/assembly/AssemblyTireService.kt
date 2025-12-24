package com.rfz.appflotal.data.network.client.assembly

import com.rfz.appflotal.data.model.assembly.AssemblyTireDto
import com.rfz.appflotal.data.model.assembly.AssemblyTireResponse
import com.rfz.appflotal.data.model.message.response.GeneralResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query


interface AssemblyTireService {
    @POST("api/AssemblyTire/CrudAssemblyTire")
    suspend fun createAssemblyTire(
        @Header("Authorization") token: String,
        @Body assemblyTire: AssemblyTireDto
    ): Response<List<GeneralResponse>>

    @GET("api/AssemblyTire/GetAssemblyTire")
    suspend fun getMountedTires(
        @Header("Authorization") token: String,
        @Query("id_monitor") idMonitor: Int
    ): Response<List<AssemblyTireResponse>>
}