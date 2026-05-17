package com.rfz.appflotal.data.repository.forum

import com.rfz.appflotal.data.model.forum.ForumResult
import com.rfz.appflotal.data.model.forum.GetForumsResponse
import com.rfz.appflotal.data.model.forum.GetTopicsResponse
import com.rfz.appflotal.data.model.forum.TopicMessageResult
import com.rfz.appflotal.data.network.service.ApiResult
import com.rfz.appflotal.data.network.service.forum.ForumService
import javax.inject.Inject

class ForumRepository @Inject constructor(
    private val forumService: ForumService
) {
    suspend fun getForums(pageNumber: Int, title: String? = null): ApiResult<GetForumsResponse?> {
        return forumService.getForums(pageNumber, title)
    }

    suspend fun getForumsById(idForum: Int): ApiResult<List<ForumResult>?> {
        return forumService.getForumsById(idForum)
    }

    suspend fun getTopics(
        pageNumber: Int,
        idForum: Int,
        title: String = "",
        tipoOrdenamiento: Int = 1
    ): ApiResult<GetTopicsResponse?> {
        return forumService.getTopics(pageNumber, idForum, title, tipoOrdenamiento)
    }

    suspend fun getTopicMessages(idTopic: Int): ApiResult<List<TopicMessageResult>?> {
        return forumService.getTopicMessages(idTopic)
    }
}
