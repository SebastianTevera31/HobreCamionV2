package com.rfz.appflotal.domain.login

import com.google.gson.Gson
import com.rfz.appflotal.data.model.login.response.LoginErrorResponse
import com.rfz.appflotal.data.model.login.response.LoginResponse
import com.rfz.appflotal.data.model.login.response.Result
import com.rfz.appflotal.data.repository.login.LoginRepository
import javax.inject.Inject
class LoginUseCase @Inject constructor(
    private val repository: LoginRepository
) {
    private val gson = Gson()

    suspend operator fun invoke(
        usuario: String,
        password: String
    ): Result<LoginResponse> {
        return try {
            val response = repository.doLogin(usuario, password)
            if (response.isSuccessful) {
                response.body()?.firstOrNull()?.let { loginResponse ->
                    Result.Success(loginResponse)
                } ?: Result.Failure(Exception("Cuerpo de la respuesta vacío"))
            } else {
                val errorMsg = response.errorBody()?.string()
                val parsedError = try {
                    gson.fromJson(errorMsg, LoginErrorResponse::class.java).message.errorValue
                } catch (e: Exception) {
                    "Error desconocido del servidor"
                }
                Result.Failure(Exception(parsedError))
            }
        } catch (e: Exception) {
           Result.Failure(e)
        }
    }
}

