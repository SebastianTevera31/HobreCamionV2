package com.rfz.appflotal.data.network.client.assembly

import com.rfz.appflotal.data.model.assembly.AssemblyTireDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST


interface AssemblyTireService {
    @POST("api/AssemblyTire/CrudAssemblyTire")
    suspend fun createAssemblyTire(
        @Header("Authorization") token: String,
        @Body assemblyTire: AssemblyTireDto
    ): Response<Unit>
}