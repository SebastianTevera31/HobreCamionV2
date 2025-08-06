package com.rfz.appflotal.data.network

import android.util.Log
import com.rfz.appflotal.data.network.service.ResultApi
import retrofit2.Response
import java.net.SocketTimeoutException

suspend fun <T> requestHelper(
    endpointName: String = "",
    request: suspend () -> Response<T>
): ResultApi<T?> {
    try {
        val response = request()
        Log.d(endpointName, "${response.code()}, ${response.message()}, ${response.body()}")
        if (response.isSuccessful) {
            Log.d(endpointName, response.message())
            if (response.body() != null) {
                Log.d(endpointName, "${response.body()}")
                return ResultApi.Success(response.body())
            } else {
                Log.e(endpointName, "El body del response esta vacio")
                return ResultApi.Error(message = "La respuesta esta vacia")
            }
        }
        return ResultApi.Error(message = "${response.message()}")
    } catch (e: Exception) {
        Log.e(endpointName, "$e")
        return ResultApi.Error(exception = e)
    } catch (e: SocketTimeoutException) {
        Log.d(endpointName, "Conexión agotada: servidor no respondió a tiempo $e")
        return ResultApi.Error(exception = e)
    }
}