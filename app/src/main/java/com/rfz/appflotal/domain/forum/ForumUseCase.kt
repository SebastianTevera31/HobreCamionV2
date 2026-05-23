package com.rfz.appflotal.domain.forum

import com.rfz.appflotal.data.mapper.toComment
import com.rfz.appflotal.data.mapper.toPost
import com.rfz.appflotal.data.mapper.toTopic
import com.rfz.appflotal.data.model.forum.CreateReportRequest
import com.rfz.appflotal.data.model.forum.CrudTopicMessageRequest
import com.rfz.appflotal.data.model.forum.CrudTopicRequest
import com.rfz.appflotal.data.model.forum.DoLikeRequest
import com.rfz.appflotal.data.model.forum.LikedPostResult
import com.rfz.appflotal.data.model.tpms.TpmsResponse
import com.rfz.appflotal.data.network.service.ApiResult
import com.rfz.appflotal.data.repository.forum.ForumRepository
import com.rfz.appflotal.presentation.ui.forums.viewmodel.Comment
import com.rfz.appflotal.presentation.ui.forums.viewmodel.Post
import com.rfz.appflotal.presentation.ui.forums.viewmodel.Topic
import javax.inject.Inject

class ForumUseCase @Inject constructor(private val forumRepository: ForumRepository) {
    suspend fun getForums(pageNumber: Int, title: String? = null): ApiResult<List<Topic>> {
        return when (val response = forumRepository.getForums(pageNumber, title)) {
            is ApiResult.Success -> ApiResult.Success(
                response.data?.results?.map { it.toTopic() } ?: emptyList()
            )

            is ApiResult.Error -> ApiResult.Error(response.exception, response.message)
            ApiResult.Loading -> ApiResult.Loading
        }
    }

    suspend fun getForumsById(idForum: Int): ApiResult<Topic?> {
        return when (val response = forumRepository.getForumsById(idForum)) {
            is ApiResult.Success -> ApiResult.Success(response.data?.firstOrNull()?.toTopic())
            is ApiResult.Error -> ApiResult.Error(response.exception, response.message)
            ApiResult.Loading -> ApiResult.Loading
        }
    }

    suspend fun getTopics(
        pageNumber: Int,
        idForum: Int,
        title: String = "",
        tipoOrdenamiento: Int = 1
    ): ApiResult<List<Post>> {
        val response = forumRepository.getTopics(pageNumber, idForum, title, tipoOrdenamiento)
        return when (response) {
            is ApiResult.Success -> ApiResult.Success(
                response.data?.results?.map { it.toPost() } ?: emptyList()
            )

            is ApiResult.Error -> ApiResult.Error(response.exception, response.message)
            ApiResult.Loading -> ApiResult.Loading
        }
    }

    suspend fun getTopicMessages(idTopic: Int): ApiResult<List<Comment>> {
        return when (val response = forumRepository.getTopicMessages(idTopic)) {
            is ApiResult.Success -> ApiResult.Success(
                response.data?.map { it.toComment() } ?: emptyList()
            )

            is ApiResult.Error -> ApiResult.Error(response.exception, response.message)
            ApiResult.Loading -> ApiResult.Loading
        }
    }

    suspend fun getRoomWithPosts(roomId: Int): ApiResult<Pair<Topic?, List<Post>>> {
        val forumResponse = forumRepository.getForumsById(roomId)
        val room = if (forumResponse is ApiResult.Success) {
            forumResponse.data?.firstOrNull()?.toTopic()
        } else null

        val topicsResponse = forumRepository.getTopics(pageNumber = 1, idForum = roomId)
        return when (topicsResponse) {
            is ApiResult.Success -> ApiResult.Success(
                Pair(
                    room,
                    topicsResponse.data?.results?.map { it.toPost() } ?: emptyList()
                )
            )

            is ApiResult.Error -> ApiResult.Error(topicsResponse.exception, topicsResponse.message)
            ApiResult.Loading -> ApiResult.Loading
        }
    }

    suspend fun crudComment(
        idTopic: Int,
        message: String,
        registrationDate: String,
        image: String = ""
    ): ApiResult<List<TpmsResponse>?> {
        return forumRepository.crudComment(
            CrudTopicMessageRequest(
                idTopicMessage = 0,
                message = message,
                registrationDate = registrationDate,
                idTopic = idTopic,
                image = image
            )
        )
    }

    suspend fun crudPost(
        title: String,
        description: String,
        color: String,
        image: String,
        idForum: Int,
        tags: String,
        registrationDate: String
    ): ApiResult<List<TpmsResponse>?> {
        return forumRepository.crudPost(
            CrudTopicRequest(
                idTopic = 0,
                title = title,
                description = description,
                color = color,
                image = image,
                idForum = idForum,
                tags = tags,
                registrationDate = registrationDate
            )
        )
    }

    suspend fun doLike(
        likedDate: String,
        idTopic: Int,
        idMessage: Int
    ): ApiResult<List<TpmsResponse>?> {
        return forumRepository.doLike(
            DoLikeRequest(
                likedDate = likedDate,
                idTopic = idTopic,
                idMessage = idMessage
            )
        )
    }

    suspend fun getLikedPosts(): ApiResult<List<LikedPostResult>> {
        return when (val response = forumRepository.getLikedPosts()) {
            is ApiResult.Success -> ApiResult.Success(response.data ?: emptyList())
            is ApiResult.Error -> ApiResult.Error(response.exception, response.message)
            ApiResult.Loading -> ApiResult.Loading
        }
    }

    suspend fun createReport(
        idTopic: Int,
        idMessage: Int,
        reportTypeId: Int,
        reportDate: String
    ): ApiResult<List<TpmsResponse>?> {
        return forumRepository.createReport(
            CreateReportRequest(
                idTopic = idTopic,
                idMessage = idMessage,
                reportDate = reportDate,
                idTypeReport = reportTypeId
            )
        )
    }
}
