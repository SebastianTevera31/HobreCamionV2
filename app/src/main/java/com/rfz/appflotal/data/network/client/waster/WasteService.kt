package com.rfz.appflotal.data.network.client.waster

import com.rfz.appflotal.data.model.message.response.MessageResponse
import com.rfz.appflotal.data.model.waster.ScrapTirePileDto
import com.rfz.appflotal.data.model.waster.response.WasteReportListResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface WasteService {

    @GET("api/Catalog/WasteReportList")
    suspend fun doWasteReportList(@Header("Authorization") token: String): Response<List<WasteReportListResponse>>

    @POST("api/ScrapTire/ScrapTire")
    suspend fun postTireToScrap(
        @Header("Authorization") token: String,
        @Body scrapTirePileDto: ScrapTirePileDto
    ): Response<List<MessageResponse>>
}