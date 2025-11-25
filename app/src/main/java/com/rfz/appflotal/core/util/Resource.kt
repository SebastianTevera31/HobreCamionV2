package com.rfz.appflotal.core.util

sealed class Resource<out T>{
    data class Success<out T:Any> (val data:T): Resource<T>()
    data class Error(val errorMessage:String): Resource<Nothing>()
    data class Loading<out T:Any>(val data:T? = null, val message:String? = null):
        Resource<T>()
}