package com.rfz.appflotal.data.network.client.forum

import com.rfz.appflotal.data.model.forum.GetForumsResponse
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
}