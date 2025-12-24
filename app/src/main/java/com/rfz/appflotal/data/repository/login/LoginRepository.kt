package com.rfz.appflotal.data.repository.login


import com.rfz.appflotal.data.model.login.dto.LoginDto
import com.rfz.appflotal.data.model.login.response.LoginResponse
import com.rfz.appflotal.data.model.login.response.RegisterBody
import com.rfz.appflotal.data.model.login.response.UpdateUserBody
import com.rfz.appflotal.data.model.message.response.GeneralResponse
import com.rfz.appflotal.data.network.service.ApiResult
import com.rfz.appflotal.data.network.service.login.LoginService
import retrofit2.Response
import javax.inject.Inject

class LoginRepository @Inject constructor(private val loginService: LoginService) {
    suspend fun doLogin(user: String, password: String, fcmToken: String): Response<List<LoginResponse>> {
        return loginService.doLogin(LoginDto(user, password, fcmToken))
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
    ): ApiResult<List<GeneralResponse>?> {
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
    ): ApiResult<List<GeneralResponse>?> {
        return loginService.doUpdateUser(
            UpdateUserBody(
                fldName = name,
                fldEmail = email,
                fldPassword = password,
                idCountry = idCountry,
                idSector = idSector,
            )
        )
    }

    suspend fun doAcceptTermsAndConditions(): ApiResult<List<GeneralResponse>?> {
        return loginService.doAcceptTermsAndConditions()
    }
}