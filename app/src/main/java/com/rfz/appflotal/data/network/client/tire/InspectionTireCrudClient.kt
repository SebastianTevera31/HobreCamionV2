package com.rfz.appflotal.data.network.client.tire

import com.rfz.appflotal.data.model.message.response.GeneralResponse

import com.rfz.appflotal.data.model.tire.dto.InspectionTireDto

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface InspectionTireCrudClient {

    @POST("api/InspectionTire")
    suspend fun doInspectionTire(@Header("Authorization") token: String, @Body requestBody: InspectionTireDto, ): Response<List<GeneralResponse>>
}