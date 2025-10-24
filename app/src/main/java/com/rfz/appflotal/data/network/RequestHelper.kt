package com.rfz.appflotal.data.network

import android.util.Log
import androidx.annotation.Keep
import com.rfz.appflotal.data.network.service.ApiResult
import retrofit2.Response
import java.net.SocketTimeoutException

@Keep
suspend fun <T> requestHelper(
    endpointName: String = "",
    request: suspend () -> Response<T>
): ApiResult<T?> {
    try {
        val response = request()
        Log.d(endpointName, "${response.code()}, ${response.message()}, ${response.body()}")
        if (response.isSuccessful) {
            Log.d(endpointName, response.message())
            if (response.body() != null) {
                Log.d(endpointName, "${response.body()}")
                return ApiResult.Success(response.body())
            } else {
                Log.e(endpointName, "El body del response esta vacio")
                return ApiResult.Error(message = "La respuesta esta vacia")
            }
        }
        return ApiResult.Error(message = "${response.message()}")
    } catch (e: Exception) {
        Log.e(endpointName, "$e")
        return ApiResult.Error(exception = e)
    } catch (e: SocketTimeoutException) {
        Log.d(endpointName, "Conexión agotada: servidor no respondió a tiempo $e")
        return ApiResult.Error(exception = e)
    }
}