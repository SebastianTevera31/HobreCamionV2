package com.rfz.appflotal.data.repository.login


import com.rfz.appflotal.data.model.login.dto.LoginDto
import com.rfz.appflotal.data.model.login.response.LoginResponse
import com.rfz.appflotal.data.model.login.response.RegisterBody
import com.rfz.appflotal.data.model.message.response.MessageResponse
import com.rfz.appflotal.data.network.service.ApiResult
import com.rfz.appflotal.data.network.service.login.LoginService
import retrofit2.Response
import javax.inject.Inject

class LoginRepository @Inject constructor(private val loginService: LoginService) {
    suspend fun doLogin(usuario: String, password: String): Response<List<LoginResponse>> {
        return loginService.doLogin(LoginDto(password, usuario))
    }

    suspend fun doRegisterUser(
        name: String,
        username: String,
        email: String,
        password: String,
        idCountry: Int,
        idSector: Int,
        typeVehicle: String,
        plates: String
    ): ApiResult<List<MessageResponse>?> {
        return loginService.doRegisterUser(
            RegisterBody(
                fldName = name,
                fldUsername = username,
                fldEmail = email,
                fldPassword = password,
                idCountry = idCountry,
                idSector = idSector,
                typeVehicle = typeVehicle,
                plates = plates
            )
        )
    }
}

