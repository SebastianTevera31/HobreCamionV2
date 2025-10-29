package com.rfz.appflotal.data.repository.login


import com.rfz.appflotal.data.model.login.dto.LoginDto
import com.rfz.appflotal.data.model.login.response.LoginResponse
import com.rfz.appflotal.data.model.login.response.RegisterBody
import com.rfz.appflotal.data.model.login.response.UpdateUserBody
import com.rfz.appflotal.data.model.message.response.MessageResponse
import com.rfz.appflotal.data.network.service.ApiResult
import com.rfz.appflotal.data.network.service.login.LoginService
import retrofit2.Response
import javax.inject.Inject

class LoginRepository @Inject constructor(private val loginService: LoginService) {
    suspend fun doLogin(user: String, password: String): Response<List<LoginResponse>> {
        return loginService.doLogin(LoginDto(user, password))
    }

    suspend fun doRegisterUser(
        name: String,
        email: String,
        password: String,
        idCountry: Int,
        idSector: Int,
        typeVehicle: String,
        plates: String,
        termsGranted: Boolean,
        registerDate: String
    ): ApiResult<List<MessageResponse>?> {
        return loginService.doRegisterUser(
            RegisterBody(
                fldName = name,
                fldEmail = email,
                fldPassword = password,
                idCountry = idCountry,
                idSector = idSector,
                typeVehicle = typeVehicle,
                plates = plates,
                termsGranted = termsGranted,
                registerDate = registerDate
            )
        )
    }

    suspend fun doUpdateUser(
        name: String,
        email: String,
        password: String,
        idCountry: Int,
        idSector: Int,
        typeVehicle: String,
        plates: String
    ): ApiResult<List<MessageResponse>?> {
        return loginService.doUpdateUser(
            UpdateUserBody(
                fldName = name,
                fldEmail = email,
                fldPassword = password,
                idCountry = idCountry,
                idSector = idSector,
                typeVehicle = typeVehicle,
                plates = plates,
            )
        )
    }

    suspend fun doAcceptTermsAndConditions(): ApiResult<List<MessageResponse>?> {
        return loginService.doAcceptTermsAndConditions()
    }
}