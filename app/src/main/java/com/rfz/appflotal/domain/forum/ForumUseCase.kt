package com.rfz.appflotal.domain.forum

import com.rfz.appflotal.data.model.forum.ForumResult
import com.rfz.appflotal.data.model.forum.GetForumsResponse
import com.rfz.appflotal.data.model.forum.GetTopicsResponse
import com.rfz.appflotal.data.model.forum.TopicMessageResult
import com.rfz.appflotal.data.network.service.ApiResult
import com.rfz.appflotal.data.repository.forum.ForumRepository
import javax.inject.Inject

class ForumUseCase @Inject constructor(
    private val forumRepository: ForumRepository
) {
    suspend fun getForums(pageNumber: Int, title: String? = null): ApiResult<GetForumsResponse?> {
        return forumRepository.getForums(pageNumber, title)
    }

    suspend fun getForumsById(idForum: Int): ApiResult<List<ForumResult>?> {
        return forumRepository.getForumsById(idForum)
    }

    suspend fun getTopics(
        pageNumber: Int,
        idForum: Int,
        title: String = "",
        tipoOrdenamiento: Int = 1
    ): ApiResult<GetTopicsResponse?> {
        return forumRepository.getTopics(pageNumber, idForum, title, tipoOrdenamiento)
    }

    suspend fun getTopicMessages(idTopic: Int): ApiResult<List<TopicMessageResult>?> {
        return forumRepository.getTopicMessages(idTopic)
    }
}
