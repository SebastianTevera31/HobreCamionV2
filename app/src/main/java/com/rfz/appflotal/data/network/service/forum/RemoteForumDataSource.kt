package com.rfz.appflotal.data.network.service.forum

import android.content.Context
import androidx.core.net.toUri
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
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.net.URI
import javax.inject.Inject

class ForumService @Inject constructor(
    private val forumClient: ForumClient,
    private val getTasksUseCase: GetTasksUseCase,
    @ApplicationContext private val context: Context
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

            val idTopicMessagePart =
                MultipartBody.Part.createFormData(
                    "id_topicMessage",
                    request.idTopicMessage.toString()
                )
            val messagePart = MultipartBody.Part.createFormData("message", request.message)
            val registrationDatePart =
                MultipartBody.Part.createFormData("registrationDate", request.registrationDate)
            val idTopicPart =
                MultipartBody.Part.createFormData("id_topic", request.idTopic.toString())
            val idTopicMessageFkPart =
                MultipartBody.Part.createFormData(
                    "id_topicMessage_fk",
                    request.fkTopicMsg?.toString() ?: "0"
                )

            val imagePart = prepareImagePart(request.image)

            forumClient.crudComment(
                "bearer $token",
                idTopicMessage = idTopicMessagePart,
                message = messagePart,
                registrationDate = registrationDatePart,
                idTopic = idTopicPart,
                image = imagePart,
                idTopicMessageFk = idTopicMessageFkPart,
            )
        }
    }

    suspend fun crudPost(request: CrudTopicRequest): ApiResult<List<TpmsResponse>?> {
        return requestHelper("crudTopic") {
            val token = getTasksUseCase().first()[0].fld_token

            val idTopicPart =
                MultipartBody.Part.createFormData("id_topic", request.idTopic.toString())
            val titlePart = MultipartBody.Part.createFormData("title", request.title)
            val descriptionPart =
                MultipartBody.Part.createFormData("description", request.description)
            val colorPart = MultipartBody.Part.createFormData("color", request.color)
            val idForumPart =
                MultipartBody.Part.createFormData("id_forum", request.idForum.toString())
            val tagsPart = MultipartBody.Part.createFormData("tags", request.tags)
            val registrationDatePart =
                MultipartBody.Part.createFormData("registrationDate", request.registrationDate)

            val imagePart = prepareImagePart(request.image)

            forumClient.crudPost(
                "bearer $token",
                idTopicPart,
                titlePart,
                descriptionPart,
                colorPart,
                imagePart,
                idForumPart,
                tagsPart,
                registrationDatePart
            )
        }
    }

    private fun prepareImagePart(imagePath: String): MultipartBody.Part? {
        if (imagePath.isEmpty()) {
            val emptyBody = ByteArray(0).toRequestBody("image/jpeg".toMediaTypeOrNull())
            return MultipartBody.Part.createFormData("image", "empty.jpg", emptyBody)
        }

        return try {
            val uri = imagePath.toUri()
            val inputStream = context.contentResolver.openInputStream(uri)
            val bytes = inputStream?.readBytes()
            inputStream?.close()

            if (bytes != null) {
                val requestFile = bytes.toRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("image", "upload.jpg", requestFile)
            } else {
                null
            }
        } catch (e: Exception) {
            try {
                val file = File(URI.create(imagePath))
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("image", file.name, requestFile)
            } catch (ex: Exception) {
                null
            }
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
