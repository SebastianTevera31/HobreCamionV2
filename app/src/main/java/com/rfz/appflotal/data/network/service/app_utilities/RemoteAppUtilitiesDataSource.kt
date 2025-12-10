package com.rfz.appflotal.data.network.service.app_utilities

import com.rfz.appflotal.data.model.apputilities.UserOpinionDto
import com.rfz.appflotal.data.network.client.apputilities.AppUtilitiesService
import com.rfz.appflotal.data.network.networkRequestHelper
import javax.inject.Inject

class RemoteAppUtilitiesDataSource @Inject constructor(
    private val appUtilitiesService: AppUtilitiesService
) {
    suspend fun pushFeedback(token: String, userOpinion: UserOpinionDto) = networkRequestHelper {
        appUtilitiesService.userOpinion("Bearer $token", userOpinion)
    }

    suspend fun getTermsAndConditions(token: String) = networkRequestHelper {
        appUtilitiesService.getTermsAndConditions("Bearer $token")
    }
}