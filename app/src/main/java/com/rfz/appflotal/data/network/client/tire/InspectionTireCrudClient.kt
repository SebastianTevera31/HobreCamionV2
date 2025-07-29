package com.rfz.appflotal.data.network.client.tire

import com.rfz.appflotal.data.model.message.response.MessageResponse

import com.rfz.appflotal.data.model.tire.dto.InspectionTireDto
import com.rfz.appflotal.data.model.tire.dto.TireCrudDto

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface InspectionTireCrudClient {

    @POST("api/InspectionTire")
    suspend fun doInspectionTire(@Body requestBody: InspectionTireDto, @Header("Authorization") token: String): Response<List<MessageResponse>>
}