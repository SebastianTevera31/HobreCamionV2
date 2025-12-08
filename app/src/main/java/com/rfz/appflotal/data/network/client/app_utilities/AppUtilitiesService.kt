package com.rfz.appflotal.data.network.client.app_utilities

import com.rfz.appflotal.data.model.app_utilities.UserOpinionDto
import com.rfz.appflotal.data.model.message.response.MessageResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AppUtilitiesService {

    @POST("api/appUtilities/UserOpinion")
    suspend fun userOpinion(
        @Header("Authorization") token: String,
        @Body userOpinion: UserOpinionDto
    ): Response<List<MessageResponse>>
}