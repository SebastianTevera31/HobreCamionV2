package com.rfz.appflotal.data.network.client.apputilities

import com.rfz.appflotal.data.model.apputilities.TermsAndConditionsDto
import com.rfz.appflotal.data.model.apputilities.UserOpinionDto
import com.rfz.appflotal.data.model.message.response.MessageResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AppUtilitiesService {

    @POST("api/appUtilities/UserOpinion")
    suspend fun userOpinion(
        @Header("Authorization") token: String,
        @Body userOpinion: UserOpinionDto
    ): Response<List<MessageResponse>>

    @GET("api/appUtilities/TermsAndConditions")
    suspend fun getTermsAndConditions(@Header("Authorization") token: String): Response<List<TermsAndConditionsDto>>
}