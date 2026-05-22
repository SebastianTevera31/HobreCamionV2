package com.rfz.appflotal.data.network.client.forum

import com.rfz.appflotal.data.model.forum.CrudTopicMessageRequest
import com.rfz.appflotal.data.model.forum.CrudTopicRequest
import com.rfz.appflotal.data.model.forum.DoLikeRequest
import com.rfz.appflotal.data.model.forum.ForumResult
import com.rfz.appflotal.data.model.forum.GetForumsResponse
import com.rfz.appflotal.data.model.forum.GetTopicsResponse
import com.rfz.appflotal.data.model.forum.LikedPostResult
import com.rfz.appflotal.data.model.forum.TopicMessageResult
import com.rfz.appflotal.data.model.tpms.TpmsResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
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

    @GET("api/Blog/GetTopicMessages")
    suspend fun getTopicMessages(
        @Header("Authorization") token: String,
        @Query("id_topic") idTopic: Int
    ): Response<List<TopicMessageResult>>

    @POST("api/Blog/CrudTopicMessage")
    suspend fun crudComment(
        @Header("Authorization") token: String,
        @Body request: CrudTopicMessageRequest
    ): Response<List<TpmsResponse>>

    @POST("api/Blog/CrudTopic")
    suspend fun crudPost(
        @Header("Authorization") token: String,
        @Body request: CrudTopicRequest
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
}
