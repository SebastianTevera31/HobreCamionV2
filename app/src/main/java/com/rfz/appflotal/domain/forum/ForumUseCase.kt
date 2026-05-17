package com.rfz.appflotal.domain.forum

import com.rfz.appflotal.data.model.forum.GetForumsResponse
import com.rfz.appflotal.data.network.service.ApiResult
import com.rfz.appflotal.data.repository.forum.ForumRepository
import javax.inject.Inject

class ForumUseCase @Inject constructor(
    private val forumRepository: ForumRepository
) {
    suspend fun getForums(pageNumber: Int, title: String? = null): ApiResult<GetForumsResponse?> {
        return forumRepository.getForums(pageNumber, title)
    }
}