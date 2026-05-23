package com.rfz.appflotal.presentation.ui.utils

sealed interface LoadState<out T> {
    object Idle : LoadState<Nothing>
    object Loading : LoadState<Nothing>
    object Cancelled : LoadState<Nothing>
    data class Error(val message: String) : LoadState<Nothing>
    data class Success<T>(val data: T) : LoadState<T>
}