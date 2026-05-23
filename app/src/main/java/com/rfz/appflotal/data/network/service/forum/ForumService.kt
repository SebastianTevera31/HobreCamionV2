package com.rfz.appflotal.data.network.service.forum

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

    suspend fun getForumsById(idForum: Int): ApiResult<List<ForumResult>?> {
        return requestHelper("getForumsById") {
            val token = getTasksUseCase().first()[0].fld_token
            forumClient.getForumsById("bearer $token", idForum)
        }
    }

    suspend fun getTopics(
        pageNumber: Int,
        idForum: Int,
        title: String = "",
        tipoOrdenamiento: Int = 1
    ): ApiResult<GetTopicsResponse?> {
        return requestHelper("getTopics") {
            val token = getTasksUseCase().first()[0].fld_token
            forumClient.getTopics("bearer $token", pageNumber, idForum, title, tipoOrdenamiento)
        }
    }

    suspend fun getTopicsById(idTopic: Int): ApiResult<List<TopicResult>?> {
        return requestHelper("getTopicsById") {
            val token = getTasksUseCase().first()[0].fld_token
            forumClient.getTopicsById("bearer $token", idTopic)
        }
    }

    suspend fun getTopicMessages(idTopic: Int): ApiResult<List<TopicMessageResult>?> {
        return requestHelper("getTopicMessages") {
            val token = getTasksUseCase().first()[0].fld_token
            forumClient.getTopicMessages("bearer $token", idTopic)
        }
    }

    suspend fun crudComment(request: CrudTopicMessageRequest): ApiResult<List<TpmsResponse>?> {
        return requestHelper("crudTopicMessage") {
            val token = getTasksUseCase().first()[0].fld_token
            forumClient.crudComment("bearer $token", request)
        }
    }

    suspend fun crudPost(request: CrudTopicRequest): ApiResult<List<TpmsResponse>?> {
        return requestHelper("crudTopic") {
            val token = getTasksUseCase().first()[0].fld_token
            forumClient.crudPost("bearer $token", request)
        }
    }

    suspend fun doLike(request: DoLikeRequest): ApiResult<List<TpmsResponse>?> {
        return requestHelper("doLike") {
            val token = getTasksUseCase().first()[0].fld_token
            forumClient.doLike("bearer $token", request)
        }
    }

    suspend fun getLikedPosts(): ApiResult<List<LikedPostResult>?> {
        return requestHelper("getLikedPosts") {
            val token = getTasksUseCase().first()[0].fld_token
            forumClient.getLikedPosts("bearer $token")
        }
    }

    suspend fun createReport(request: CreateReportRequest): ApiResult<List<TpmsResponse>?> {
        return requestHelper("createReport") {
            val token = getTasksUseCase().first()[0].fld_token
            forumClient.createReport("bearer $token", request)
        }
    }
}
