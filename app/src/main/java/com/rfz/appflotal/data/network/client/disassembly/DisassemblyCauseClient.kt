package com.rfz.appflotal.data.network.client.disassembly


import com.rfz.appflotal.data.model.disassembly.response.DisassemblyCauseResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface DisassemblyCauseClient {

    @GET("api/Catalog/DisassemblyCause")
    suspend fun doDisassemblyCause(@Header("Authorization") token: String): Response<List<DisassemblyCauseResponse>>

}