package com.rfz.appflotal.presentation.ui.utils

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import com.rfz.appflotal.R
import com.rfz.appflotal.data.network.service.ApiResult
import com.rfz.appflotal.domain.CatalogItem


enum class FormState {
    COMPLETE,
    INCOMPLETE,
    ERROR
}

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

fun <T> responseHelperWithResult(
    response: Result<T>,
    onError: () -> Unit = {},
    onSuccess: (data: T) -> Unit
) {
    response.onSuccess {
        onSuccess(it)
    }.onFailure {
        onError()
    }
}

suspend fun <T> asyncResponseHelper(
    response: ApiResult<T>,
    onError: () -> Unit = {},
    onSuccess: suspend (data: T) -> Unit
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

fun String.toIntOrError(): Pair<Int?, Int?> {
    // Devuelve el Int (si se puede) y un posible mensaje de error
    if (isBlank()) return null to R.string.requerido
    val value = toDoubleOrNull() ?: return null to R.string.numero_invalido
    return value.toInt() to null
}

fun String.validate(): Int? {
    if (isBlank()) return R.string.requerido
    val value = toDoubleOrNull() ?: return R.string.numero_invalido
    return null
}

fun CatalogItem.validate(): Int? {
    if (description.isBlank()) return R.string.requerido
    return null
}

suspend fun SnackbarHostState.showMessage(
    message: String,
    withDismiss: Boolean = true
) {
    showSnackbar(
        message = message,
        withDismissAction = withDismiss,
        duration = SnackbarDuration.Short
    )
}