package com.rfz.appflotal.core.util

sealed class Resource<out T:Any>{
    data class Success<out T:Any> (val data:T): com.rfz.appflotal.core.util.Resource<T>()
    data class Error(val errorMessage:String): com.rfz.appflotal.core.util.Resource<Nothing>()
    data class Loading<out T:Any>(val data:T? = null, val message:String? = null):
        com.rfz.appflotal.core.util.Resource<T>()
}