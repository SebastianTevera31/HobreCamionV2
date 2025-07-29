package com.rfz.appflotal.data.repository.login


import com.rfz.appflotal.data.model.login.dto.LoginDto
import com.rfz.appflotal.data.model.login.response.LoginResponse
import com.rfz.appflotal.data.network.service.login.LoginService
import retrofit2.Response
import javax.inject.Inject

class LoginRepository @Inject constructor(private val loginService: LoginService) {
    suspend fun doLogin(usuario: String, password: String): Response<List<LoginResponse>> {
        return loginService.doLogin(LoginDto(password, usuario))
    }
}

