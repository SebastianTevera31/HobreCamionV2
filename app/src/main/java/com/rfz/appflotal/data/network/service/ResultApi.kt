package com.rfz.appflotal.data.network.service

sealed class ResultApi<out T> {
    data class Success<out T>(val data: T) : ResultApi<T>()
    data class Error(val exception: Exception? = null, val message: String? = null) :
        ResultApi<Nothing>()
}