package com.rfz.appflotal.data.repository.apputilities

import com.rfz.appflotal.data.model.app_utilities.UserOpinion
import com.rfz.appflotal.data.model.app_utilities.toDto
import com.rfz.appflotal.data.model.message.response.MessageResponse
import com.rfz.appflotal.data.network.service.app_utilities.RemoteAppUtilitiesDataSource
import com.rfz.appflotal.domain.database.GetTasksUseCase
import kotlinx.coroutines.flow.first
import javax.inject.Inject

interface AppUtilitiesRepository {
    suspend fun sendFeedback(userOpinion: UserOpinion): Result<List<MessageResponse>>
}

class AppUtilitiesRepositoryImpl @Inject constructor(
    private val remoteAppUtilitiesDataSource: RemoteAppUtilitiesDataSource,
    private val getTasksUseCase: GetTasksUseCase
) : AppUtilitiesRepository {
    override suspend fun sendFeedback(userOpinion: UserOpinion): Result<List<MessageResponse>> {
        val token = getTasksUseCase().first().first().fld_token
        return remoteAppUtilitiesDataSource.pushFeedback(token, userOpinion.toDto())
    }
}