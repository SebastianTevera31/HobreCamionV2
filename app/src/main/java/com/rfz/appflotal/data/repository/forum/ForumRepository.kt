package com.rfz.appflotal.data.repository.forum

import com.rfz.appflotal.data.model.forum.CreateReportRequest
import com.rfz.appflotal.data.model.forum.CrudTopicMessageRequest
import com.rfz.appflotal.data.model.forum.CrudTopicRequest
import com.rfz.appflotal.data.model.forum.DoLikeRequest
import com.rfz.appflotal.data.model.forum.ForumResult
import com.rfz.appflotal.data.model.forum.GetForumsResponse
import com.rfz.appflotal.data.model.forum.GetTopicsResponse
import com.rfz.appflotal.data.model.forum.LikedPostResult
import com.rfz.appflotal.data.model.forum.TopicMessageResult
import com.rfz.appflotal.data.model.forum.TopicResult
import com.rfz.appflotal.data.model.tpms.TpmsResponse
import com.rfz.appflotal.data.network.service.ApiResult
import com.rfz.appflotal.data.network.service.forum.ForumService
import javax.inject.Inject

class ForumRepository @Inject constructor(
    private val forumService: ForumService,
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

    suspend fun getTopicsById(idTopic: Int): ApiResult<List<TopicResult>?> {
        return forumService.getTopicsById(idTopic)
    }

    suspend fun getTopicMessages(idTopic: Int): ApiResult<List<TopicMessageResult>?> {
        return forumService.getTopicMessages(idTopic)
    }

    suspend fun crudComment(request: CrudTopicMessageRequest): ApiResult<List<TpmsResponse>?> {
        return forumService.crudComment(request)
    }

    suspend fun crudPost(request: CrudTopicRequest): ApiResult<List<TpmsResponse>?> {
        return forumService.crudPost(request)
    }

    suspend fun doLike(request: DoLikeRequest): ApiResult<List<TpmsResponse>?> {
        return forumService.doLike(request)
    }

    suspend fun getLikedPosts(): ApiResult<List<LikedPostResult>?> {
        return forumService.getLikedPosts()
    }

    suspend fun createReport(request: CreateReportRequest): ApiResult<List<TpmsResponse>?> {
        return forumService.createReport(request)
    }
}
