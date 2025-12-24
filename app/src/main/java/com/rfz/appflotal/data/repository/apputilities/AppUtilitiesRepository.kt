package com.rfz.appflotal.data.repository.apputilities

import com.rfz.appflotal.data.model.apputilities.TermsAndConditionsDto
import com.rfz.appflotal.data.model.apputilities.UserOpinion
import com.rfz.appflotal.data.model.apputilities.toDto
import com.rfz.appflotal.data.model.message.response.GeneralResponse
import com.rfz.appflotal.data.network.service.app_utilities.RemoteAppUtilitiesDataSource
import com.rfz.appflotal.domain.database.GetTasksUseCase
import kotlinx.coroutines.flow.first
import javax.inject.Inject

interface AppUtilitiesRepository {
    suspend fun sendFeedback(userOpinion: UserOpinion): Result<List<GeneralResponse>>
    suspend fun getTermsAndConditions(): Result<List<TermsAndConditionsDto>>
}

class AppUtilitiesRepositoryImpl @Inject constructor(
    private val remoteAppUtilitiesDataSource: RemoteAppUtilitiesDataSource,
    private val getTasksUseCase: GetTasksUseCase
) : AppUtilitiesRepository {
    override suspend fun sendFeedback(userOpinion: UserOpinion): Result<List<GeneralResponse>> {
        val token = getTasksUseCase().first().first().fld_token
        return remoteAppUtilitiesDataSource.pushFeedback(token, userOpinion.toDto())
    }

    override suspend fun getTermsAndConditions(): Result<List<TermsAndConditionsDto>> {
        val token = getTasksUseCase().first().first().fld_token
        return remoteAppUtilitiesDataSource.getTermsAndConditions(token)
    }
}