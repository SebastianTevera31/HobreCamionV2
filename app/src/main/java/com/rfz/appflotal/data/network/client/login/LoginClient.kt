package com.rfz.appflotal.data.network.client.login


import com.rfz.appflotal.data.model.login.dto.LoginDto
import com.rfz.appflotal.data.model.login.response.LoginResponse
import com.rfz.appflotal.data.model.login.response.RegisterBody
import com.rfz.appflotal.data.model.login.response.UpdateUserBody
import com.rfz.appflotal.data.model.message.response.GeneralResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT

interface LoginClient {

    @POST("api/test")
    suspend fun loginTest(): Response<ResponseBody>

    @POST("api/authenticate")
    suspend fun doLogin(@Body requestBody: LoginDto): Response<List<LoginResponse>>

    @POST("api/RegisterUser")
    suspend fun registerUser(@Body requestBody: RegisterBody): Response<List<GeneralResponse>>

    @PUT("api/UpdateUser")
    suspend fun updateUser(
        @Header("Authorization") token: String,
        @Body requestBody: UpdateUserBody
    ): Response<List<GeneralResponse>>

    @PUT("api/AcceptTermsAndConditions")
    suspend fun acceptTermsAndConditions(@Header("Authorization") token: String):
            Response<List<GeneralResponse>>
}
