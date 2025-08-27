package com.rfz.appflotal.domain.login

import android.content.Context
import android.util.Patterns
import com.google.gson.Gson
import com.rfz.appflotal.R
import com.rfz.appflotal.data.model.login.response.LoginErrorResponse
import com.rfz.appflotal.data.model.login.response.LoginResponse
import com.rfz.appflotal.data.model.login.response.Result
import com.rfz.appflotal.data.model.message.response.MessageResponse
import com.rfz.appflotal.data.network.service.ApiResult
import com.rfz.appflotal.data.repository.login.LoginRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: LoginRepository
) {
    private val gson = Gson()

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
                val errorMsg = response.errorBody()?.string()
                val parsedError = try {
                    gson.fromJson(errorMsg, LoginErrorResponse::class.java).message.errorValue
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
        username: String,
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
            username,
            email,
            password,
            idCountry,
            idSector,
            typeVehicle,
            plates
        )
    }
}

