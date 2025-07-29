package com.rfz.appflotal.data.network.client.diagram


import com.rfz.appflotal.data.model.diagram.response.DiagramResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface DiagramClient {

    @GET("api/Catalog/Diagram")
    suspend fun doDiagram(@Header("Authorization") token: String): Response<List<DiagramResponse>>

}