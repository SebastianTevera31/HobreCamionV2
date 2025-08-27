package com.rfz.appflotal.presentation.ui.utils

import com.rfz.appflotal.data.network.service.ApiResult

fun <T> responseHelper(response: ApiResult<T>, operation: (data: T) -> Unit): String? {
    return when (response) {
        is ApiResult.Success -> {
            operation(response.data)
            null
        }

        is ApiResult.Error -> {
            "${response.message}"
        }

        ApiResult.Loading -> {
            null
        }
    }
}