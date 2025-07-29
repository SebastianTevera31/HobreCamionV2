package com.rfz.appflotal.data.network.client.originaldesign

import com.rfz.appflotal.data.model.disassembly.response.DisassemblyCauseResponse
import com.rfz.appflotal.data.model.originaldesign.response.OriginalDesignResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface OriginalDesignClient {



    @GET("api/Catalog/OriginalDesign")
    suspend fun doOriginalDesign(@Header("Authorization") token: String): Response<List<OriginalDesignResponse>>

}