package com.rfz.appflotal.data.network.client.forum

import com.rfz.appflotal.data.model.forum.CreateReportRequest
import com.rfz.appflotal.data.model.forum.DoLikeRequest
import com.rfz.appflotal.data.model.forum.ForumResult
import com.rfz.appflotal.data.model.forum.GetForumsResponse
import com.rfz.appflotal.data.model.forum.GetPostsFeedResponse
import com.rfz.appflotal.data.model.forum.GetTopicsResponse
import com.rfz.appflotal.data.model.forum.LikedPostResult
import com.rfz.appflotal.data.model.forum.TopicMessageResult
import com.rfz.appflotal.data.model.forum.TopicResult
import com.rfz.appflotal.data.model.tpms.TpmsResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Query

interface ForumClient {
    @GET("api/Blog/GetForums")
    suspend fun getForums(
        @Header("Authorization") token: String,
        @Query("pageNumber") pageNumber: Int,
        @Query("title") title: String? = null
    ): Response<GetForumsResponse>

    @GET("api/Blog/GetForumsByID")
    suspend fun getForumsById(
        @Header("Authorization") token: String,
        @Query("id_forum") idForum: Int
    ): Response<List<ForumResult>>

    @GET("api/Blog/GetTopics")
    suspend fun getTopics(
        @Header("Authorization") token: String,
        @Query("pageNumber") pageNumber: Int,
        @Query("id_forum") idForum: Int,
        @Query("tittle") title: String = "",
        @Query("tipoOrdenamiento") tipoOrdenamiento: Int = 1
    ): Response<GetTopicsResponse>

    @GET("api/Blog/GetTopicsByID")
    suspend fun getTopicsById(
        @Header("Authorization") token: String,
        @Query("id_topic") idTopic: Int
    ): Response<List<TopicResult>>

    @GET("api/Blog/GetTopicMessages")
    suspend fun getTopicMessages(
        @Header("Authorization") token: String,
        @Query("id_topic") idTopic: Int
    ): Response<List<TopicMessageResult>>

    @Multipart
    @POST("api/Blog/CrudTopicMessage")
    suspend fun crudComment(
        @Header("Authorization") token: String,
        @Part idTopicMessage: MultipartBody.Part,
        @Part message: MultipartBody.Part,
        @Part registrationDate: MultipartBody.Part,
        @Part idTopic: MultipartBody.Part,
        @Part image: MultipartBody.Part?,
        @Part idTopicMessageFk: MultipartBody.Part
    ): Response<List<TpmsResponse>>

    @Multipart
    @POST("api/Blog/CrudTopic")
    suspend fun crudPost(
        @Header("Authorization") token: String,
        @Part idTopic: MultipartBody.Part,
        @Part title: MultipartBody.Part,
        @Part description: MultipartBody.Part,
        @Part color: MultipartBody.Part,
        @Part image: MultipartBody.Part?,
        @Part idForum: MultipartBody.Part,
        @Part tags: MultipartBody.Part,
        @Part registrationDate: MultipartBody.Part
    ): Response<List<TpmsResponse>>

    @PUT("api/Blog/DoLike")
    suspend fun doLike(
        @Header("Authorization") token: String,
        @Body request: DoLikeRequest
    ): Response<List<TpmsResponse>>

    @GET("api/Blog/GetLikedPosts")
    suspend fun getLikedPosts(
        @Header("Authorization") token: String
    ): Response<List<LikedPostResult>>

    @GET("api/Blog/GetPostsFeed")
    suspend fun getPostsFeed(
        @Header("Authorization") token: String,
        @Query("tipoFeed") tipoFeed: Int,
        @Query("id_forum") idForum: Int,
        @Query("pageNumber") pageNumber: Int,
    ): Response<GetPostsFeedResponse>

    @POST("api/Blog/CreateReport")
    suspend fun createReport(
        @Header("Authorization") token: String,
        @Body request: CreateReportRequest
    ): Response<List<TpmsResponse>>
}
