package com.rfz.appflotal.presentation.ui.utils

import com.rfz.appflotal.data.network.service.ApiResult

fun <T> responseHelper(
    response: ApiResult<T>,
    onError: () -> Unit = {},
    onSuccess: (data: T) -> Unit
) {
    return when (response) {
        is ApiResult.Success -> {
            onSuccess(response.data)
        }

        is ApiResult.Error -> {
            onError()
        }

        ApiResult.Loading -> {}
    }
}