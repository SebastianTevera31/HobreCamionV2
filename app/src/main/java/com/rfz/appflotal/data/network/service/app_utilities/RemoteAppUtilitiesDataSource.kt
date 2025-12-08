package com.rfz.appflotal.data.network.service.app_utilities

import com.rfz.appflotal.data.model.app_utilities.UserOpinionDto
import com.rfz.appflotal.data.network.client.app_utilities.AppUtilitiesService
import com.rfz.appflotal.data.network.networkRequestHelper
import javax.inject.Inject

class RemoteAppUtilitiesDataSource @Inject constructor(
    private val appUtilitiesService: AppUtilitiesService
) {
    suspend fun pushFeedback(token: String, userOpinion: UserOpinionDto) = networkRequestHelper {
        appUtilitiesService.userOpinion("Bearer $token", userOpinion)
    }
}