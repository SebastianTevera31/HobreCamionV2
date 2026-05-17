package com.rfz.appflotal.data.repository.forum

import com.rfz.appflotal.data.model.forum.GetForumsResponse
import com.rfz.appflotal.data.network.service.ApiResult
import com.rfz.appflotal.data.network.service.forum.ForumService
import javax.inject.Inject

class ForumRepository @Inject constructor(
    private val forumService: ForumService
) {
    suspend fun getForums(pageNumber: Int, title: String? = null): ApiResult<GetForumsResponse?> {
        return forumService.getForums(pageNumber, title)
    }
}