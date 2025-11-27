package com.rfz.appflotal.data.network.service

sealed class DataError : Exception() {
    data class Network(override val cause: Throwable) : DataError()
    data class Local(override val cause: Throwable) : DataError()
    data class Http(val code: Int, val body: String?) : DataError()
    class Unknown : DataError() {
        private fun readResolve(): Any = Unknown()
    }
}