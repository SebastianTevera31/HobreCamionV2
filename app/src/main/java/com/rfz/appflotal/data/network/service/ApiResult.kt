package com.rfz.appflotal.data.network.service

import androidx.annotation.Keep

@Keep
sealed class ApiResult<out T> {
    data class Success<out T>(val data: T) : ApiResult<T>()
    data class Error(val exception: Exception? = null, val message: String? = null) :
        ApiResult<Nothing>()

    object Loading : ApiResult<Nothing>()
}