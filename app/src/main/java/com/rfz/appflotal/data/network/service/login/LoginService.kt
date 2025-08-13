package com.rfz.appflotal.data.network.service.login

import com.rfz.appflotal.data.model.login.dto.LoginDto
import com.rfz.appflotal.data.model.login.response.LoginResponse
import com.rfz.appflotal.data.network.client.login.LoginClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject
import kotlin.math.log

class LoginService @Inject constructor(private val loginClient: LoginClient) {

    suspend fun doLogin(requestBody: LoginDto): Response<List<LoginResponse>> {
        return withContext(Dispatchers.IO) {
            loginClient.doLogin(requestBody)
        }
    }
}