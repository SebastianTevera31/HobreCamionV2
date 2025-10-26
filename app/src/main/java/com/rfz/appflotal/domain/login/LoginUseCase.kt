package com.rfz.appflotal.domain.login

import android.content.Context
import android.util.Patterns
import com.google.gson.Gson
import com.rfz.appflotal.R
import com.rfz.appflotal.core.util.Commons.getCurrentDate
import com.rfz.appflotal.data.model.login.response.LoginResponse
import com.rfz.appflotal.data.model.login.response.Result
import com.rfz.appflotal.data.model.message.response.MessageResponse
import com.rfz.appflotal.data.network.service.ApiResult
import com.rfz.appflotal.data.repository.login.LoginRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: LoginRepository
) {
    suspend fun doLogin(
        usuario: String,
        password: String,
        ctx: Context,
    ): Result<LoginResponse> {
        return try {
            val response = repository.doLogin(usuario, password)
            if (response.isSuccessful) {
                response.body()?.firstOrNull()?.let { loginResponse ->
                    Result.Success(loginResponse)
                } ?: Result.Failure(Exception(ctx.getString(R.string.cuerpo_de_la_respuesta_vac_o)))
            } else {
                val errorMessage = response.message()
                val parsedError = try {
                    if (errorMessage == "Unauthorized") ctx.getString(R.string.credenciales_incorrectas) else errorMessage
                } catch (e: Exception) {
                    ctx.getString(R.string.error_desconocido_del_servidor)
                }
                Result.Failure(Exception(parsedError))
            }
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    suspend fun doRegisterUser(
        name: String,
        email: String,
        password: String,
        idCountry: Int,
        idSector: Int,
        typeVehicle: String,
        plates: String
    ): ApiResult<List<MessageResponse>?> {
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches())
            return ApiResult.Error(message = "Introduzca un correo valido")

        if (password.isEmpty() || password.length < 8)
            return ApiResult.Error(message = "Introduzca una contraseña de minimo 8 caracteres")

        return repository.doRegisterUser(
            name,
            email,
            password,
            idCountry,
            idSector,
            typeVehicle,
            plates,
            true,
            getCurrentDate()
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
        return repository.doUpdateUser(
            name,
            email,
            password,
            idCountry,
            idSector,
            typeVehicle,
            plates
        )
    }
}

