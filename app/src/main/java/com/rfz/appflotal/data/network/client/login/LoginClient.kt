package com.rfz.appflotal.data.network.client.login


import com.rfz.appflotal.data.model.login.dto.LoginDto
import com.rfz.appflotal.data.model.login.response.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginClient {
    @POST("api/authenticate")
    suspend fun doLogin(@Body requestBody: LoginDto):  Response<List<LoginResponse>>
}
