package com.rfz.appflotal.data.network

import android.util.Log
import retrofit2.Response
import java.net.SocketTimeoutException

suspend fun <T> requestHelper(endpointName: String = "", request: suspend () -> Response<T>) {
    try {
        val response = request()
        Log.d(endpointName, "${response.code()}")
        if (response.isSuccessful) {
            Log.d(endpointName, response.message())
            if (response.body() != null) Log.d(endpointName, "${response.body()}")
            else Log.e(endpointName, "El body del response esta vacio")
        }
    } catch (e: Exception) {
        Log.e(endpointName, "${e.message}")
    } catch (e: SocketTimeoutException) {
        Log.d(endpointName, "Conexión agotada: servidor no respondió a tiempo $e")
    }
}