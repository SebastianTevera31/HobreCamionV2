package com.rfz.appflotal.data.network.client.disassembly


import com.rfz.appflotal.data.model.disassembly.response.DisassemblyCauseResponse
import com.rfz.appflotal.data.model.disassembly.tire.DisassemblyTireRequestDto
import com.rfz.appflotal.data.model.message.response.MessageResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface DisassemblyCauseService {

    @GET("api/Catalog/DisassemblyCause")
    suspend fun doDisassemblyCause(@Header("Authorization") token: String): Response<List<DisassemblyCauseResponse>>

    @POST("api/Disassembly/DisassemblyTire")
    suspend fun createDisassemblyTire(
        @Header("Authorization") token: String,
        @Body request: DisassemblyTireRequestDto
    ): Response<List<MessageResponse>>
}