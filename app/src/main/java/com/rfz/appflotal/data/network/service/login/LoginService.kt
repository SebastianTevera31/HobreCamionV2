package com.rfz.appflotal.data.network.service.login

import com.rfz.appflotal.data.model.login.dto.LoginDto
import com.rfz.appflotal.data.model.login.response.LoginResponse
import com.rfz.appflotal.data.model.login.response.RegisterBody
import com.rfz.appflotal.data.model.login.response.UpdateUserBody
import com.rfz.appflotal.data.model.message.response.MessageResponse
import com.rfz.appflotal.data.network.client.login.LoginClient
import com.rfz.appflotal.data.network.requestHelper
import com.rfz.appflotal.data.network.service.ApiResult
import com.rfz.appflotal.domain.database.GetTasksUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject

class LoginService @Inject constructor(
    private val loginClient: LoginClient,
    private val getTasksUseCase: GetTasksUseCase
) {
    suspend fun doLogin(requestBody: LoginDto): Response<List<LoginResponse>> {
        return withContext(Dispatchers.IO) {
            loginClient.doLogin(requestBody)
        }
    }

    suspend fun doRegisterUser(requestBody: RegisterBody): ApiResult<List<MessageResponse>?> {
        return requestHelper(endpointName = "RegisterUser") {
            loginClient.registerUser(requestBody)
        }
    }

    suspend fun doUpdateUser(requestBody: UpdateUserBody): ApiResult<List<MessageResponse>?> {
        return requestHelper(endpointName = "UpdateUser") {
            val token = getTasksUseCase().first()[0].fld_token
            loginClient.updateUser("bearer $token", requestBody)
        }
    }
}