package com.rfz.appflotal.data.network.client.forum

import com.rfz.appflotal.data.model.forum.ForumResult
import com.rfz.appflotal.data.model.forum.GetForumsResponse
import com.rfz.appflotal.data.model.forum.GetTopicsResponse
import com.rfz.appflotal.data.model.forum.TopicMessageResult
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
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
}
