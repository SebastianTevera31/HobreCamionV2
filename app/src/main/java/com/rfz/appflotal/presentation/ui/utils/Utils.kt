package com.rfz.appflotal.presentation.ui.utils

import com.rfz.appflotal.data.network.service.ApiResult

fun <T> responseHelper(
    response: ApiResult<T>,
    operation: (data: T) -> Unit
) {
    return when (response) {
        is ApiResult.Success -> {
            operation(response.data)
        }

        is ApiResult.Error -> {

        }

        ApiResult.Loading -> {}
    }
}