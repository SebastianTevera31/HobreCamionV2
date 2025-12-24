package com.rfz.appflotal.data.network

import android.util.Log
import androidx.annotation.Keep
import com.rfz.appflotal.data.network.service.ApiResult
import com.rfz.appflotal.data.network.service.DataError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
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

suspend fun <T> networkRequestHelper(request: suspend () -> Response<T>) =
    withContext(Dispatchers.IO) {
        try {
            val res = request()
            if (res.isSuccessful && res.body() != null) {
                Result.success(res.body()!!)
            } else {
                Result.failure(DataError.Http(res.code(), res.errorBody()?.string()))
            }
        } catch (e: IOException) {
            Result.failure(DataError.Network(e))
        } catch (e: HttpException) {
            Result.failure(DataError.Http(e.code(), e.message()))
        } catch (e: Exception) {
            Log.d(e.message.toString(), e.message.toString())
            Result.failure(DataError.Unknown())
        }
    }
