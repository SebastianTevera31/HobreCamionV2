package com.rfz.appflotal.domain.forum

import com.rfz.appflotal.data.model.forum.ForumComment
import com.rfz.appflotal.data.model.forum.CreateReportRequest
import com.rfz.appflotal.data.model.forum.CrudTopicMessageRequest
import com.rfz.appflotal.data.model.forum.CrudTopicRequest
import com.rfz.appflotal.data.model.forum.DoLikeRequest
import com.rfz.appflotal.data.model.forum.LikedPostResult
import com.rfz.appflotal.data.model.forum.ForumRoom
import com.rfz.appflotal.data.model.forum.ForumTopic
import com.rfz.appflotal.data.model.forum.toComment
import com.rfz.appflotal.data.model.forum.toRoom
import com.rfz.appflotal.data.model.forum.toTopic
import com.rfz.appflotal.data.model.tpms.TpmsResponse
import com.rfz.appflotal.data.network.service.ApiResult
import com.rfz.appflotal.data.repository.forum.ForumRepository
import javax.inject.Inject

class GetForumRoomsUseCase @Inject constructor(private val forumRepository: ForumRepository) {
    suspend operator fun invoke(pageNumber: Int, title: String? = null): ApiResult<List<ForumRoom>> {
        return when (val response = forumRepository.getForums(pageNumber, title)) {
            is ApiResult.Success -> ApiResult.Success(
                response.data?.results?.map { it.toRoom() } ?: emptyList()
            )

            is ApiResult.Error -> ApiResult.Error(response.exception, response.message)
            ApiResult.Loading -> ApiResult.Loading
        }
    }
}

class GetForumRoomByIdUseCase @Inject constructor(private val forumRepository: ForumRepository) {
    suspend operator fun invoke(idForum: Int): ApiResult<ForumRoom?> {
        return when (val response = forumRepository.getForumsById(idForum)) {
            is ApiResult.Success -> ApiResult.Success(response.data?.firstOrNull()?.toRoom())
            is ApiResult.Error -> ApiResult.Error(response.exception, response.message)
            ApiResult.Loading -> ApiResult.Loading
        }
    }
}

class GetForumTopicsUseCase @Inject constructor(private val forumRepository: ForumRepository) {
    suspend operator fun invoke(
        pageNumber: Int,
        idForum: Int,
        title: String = "",
        tipoOrdenamiento: Int = 1
    ): ApiResult<List<ForumTopic>> {
        val response = forumRepository.getTopics(pageNumber, idForum, title, tipoOrdenamiento)
        return when (response) {
            is ApiResult.Success -> ApiResult.Success(
                response.data?.results?.map { it.toTopic() } ?: emptyList()
            )

            is ApiResult.Error -> ApiResult.Error(response.exception, response.message)
            ApiResult.Loading -> ApiResult.Loading
        }
    }
}

class GetForumTopicByIdUseCase @Inject constructor(private val forumRepository: ForumRepository) {
    suspend operator fun invoke(idTopic: Int): ApiResult<ForumTopic?> {
        return when (val response = forumRepository.getTopicsById(idTopic)) {
            is ApiResult.Success -> ApiResult.Success(response.data?.firstOrNull()?.toTopic())
            is ApiResult.Error -> ApiResult.Error(response.exception, response.message)
            ApiResult.Loading -> ApiResult.Loading
        }
    }
}

class GetForumTopicMessagesUseCase @Inject constructor(private val forumRepository: ForumRepository) {
    suspend operator fun invoke(idTopic: Int): ApiResult<List<ForumComment>> {
        return when (val response = forumRepository.getTopicMessages(idTopic)) {
            is ApiResult.Success -> ApiResult.Success(
                response.data?.map { it.toComment() } ?: emptyList()
            )

            is ApiResult.Error -> ApiResult.Error(response.exception, response.message)
            ApiResult.Loading -> ApiResult.Loading
        }
    }
}

class GetForumRoomWithTopicsUseCase @Inject constructor(private val forumRepository: ForumRepository) {
    suspend operator fun invoke(roomId: Int): ApiResult<Pair<ForumRoom?, List<ForumTopic>>> {
        val forumResponse = forumRepository.getForumsById(roomId)
        val room = if (forumResponse is ApiResult.Success) {
            forumResponse.data?.firstOrNull()?.toRoom()
        } else null

        val topicsResponse = forumRepository.getTopics(pageNumber = 1, idForum = roomId)
        return when (topicsResponse) {
            is ApiResult.Success -> ApiResult.Success(
                Pair(
                    room,
                    topicsResponse.data?.results?.map { it.toTopic() } ?: emptyList()
                )
            )

            is ApiResult.Error -> ApiResult.Error(topicsResponse.exception, topicsResponse.message)
            ApiResult.Loading -> ApiResult.Loading
        }
    }
}

class CrudForumCommentUseCase @Inject constructor(private val forumRepository: ForumRepository) {
    suspend operator fun invoke(
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
}

class CrudForumTopicUseCase @Inject constructor(private val forumRepository: ForumRepository) {
    suspend operator fun invoke(
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
}

class DoForumLikeUseCase @Inject constructor(private val forumRepository: ForumRepository) {
    suspend operator fun invoke(
        likedDate: String,
        tipoElemento: Boolean,
        idMessage: Int
    ): ApiResult<List<TpmsResponse>?> {
        return forumRepository.doLike(
            DoLikeRequest(
                likedDate = likedDate,
                tipoElemento = tipoElemento,
                idElemento = idMessage
            )
        )
    }
}

class GetLikedPostsUseCase @Inject constructor(private val forumRepository: ForumRepository) {
    suspend operator fun invoke(): ApiResult<List<LikedPostResult>> {
        return when (val response = forumRepository.getLikedPosts()) {
            is ApiResult.Success -> ApiResult.Success(response.data ?: emptyList())
            is ApiResult.Error -> ApiResult.Error(response.exception, response.message)
            ApiResult.Loading -> ApiResult.Loading
        }
    }
}

class CreateForumReportUseCase @Inject constructor(private val forumRepository: ForumRepository) {
    suspend operator fun invoke(
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
