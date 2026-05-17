package com.rfz.appflotal.data.network.service.forum

import com.rfz.appflotal.data.model.forum.GetForumsResponse
import com.rfz.appflotal.data.network.client.forum.ForumClient
import com.rfz.appflotal.data.network.requestHelper
import com.rfz.appflotal.data.network.service.ApiResult
import com.rfz.appflotal.domain.database.GetTasksUseCase
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ForumService @Inject constructor(
    private val forumClient: ForumClient,
    private val getTasksUseCase: GetTasksUseCase
) {
    suspend fun getForums(pageNumber: Int, title: String? = null): ApiResult<GetForumsResponse?> {
        return requestHelper("getForums") {
            val token = getTasksUseCase().first()[0].fld_token
            forumClient.getForums("bearer $token", pageNumber, title)
        }
    }
}