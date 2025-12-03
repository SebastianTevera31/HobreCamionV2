package com.rfz.appflotal.data.network.client.tire

import com.rfz.appflotal.data.model.message.response.MessageResponse
import com.rfz.appflotal.data.model.tire.RepairedTireDto
import com.rfz.appflotal.data.model.tire.RetreatedTire
import com.rfz.appflotal.data.model.tire.RetreatedTireDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface TireService {
    @POST("api/Tire/RetreadedTire")
    suspend fun postRetreatedTire(
        @Header("Authorization") token: String,
        @Body request: RetreatedTireDto
    ): Response<List<MessageResponse>>

    @POST("api/Tire/RepairedTire")
    suspend fun postRepairedTire(
        @Header("Authorization") token: String,
        @Body body: RepairedTireDto
    ): Response<List<MessageResponse>>
}