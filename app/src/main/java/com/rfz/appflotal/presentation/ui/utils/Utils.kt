package com.rfz.appflotal.presentation.ui.utils

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import com.rfz.appflotal.R
import com.rfz.appflotal.data.model.CatalogItem
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

fun String.validateOdometer(lastOdometer: Int): Pair<Int?, Int?> {
    if (isBlank()) return null to R.string.requerido
    val value = toDoubleOrNull() ?: return null to R.string.numero_invalido
    if (value.toInt() < lastOdometer) return null to R.string.valor_invalido
    return value.toInt() to null
}

fun CatalogItem?.validate(): Int? {
    if (this == null) return R.string.requerido
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

sealed class OperationStatus {
    object Loading : OperationStatus()
    object Error : OperationStatus()
    object Success : OperationStatus()
}

enum class SubScreens {
    LIST,
    HOME
}

enum class FireCloudMessagingType(val value: String) {
    ACTUALIZACION("Actualización"),
    TERMINOS("Terminos y Condiciones"),
    CAMBIO_DE_PLAN("Cambio de Plan"),
    SERVICIO_AUTO("Servicio de Auto"),
    MANTENIMIENTO("Mantenimiento"),
    ARREGLO_URGENTE("Arreglo Urgente"),
    NONE("Sin evento")
}
