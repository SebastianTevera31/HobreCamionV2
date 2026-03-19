package com.rfz.appflotal.presentation.ui.utils

import android.Manifest
import android.app.ActivityManager
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.core.content.ContextCompat
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

fun String.toFloatOrError(): Pair<Float?, Int?> {
    // Devuelve el Int (si se puede) y un posible mensaje de error
    if (isBlank()) return null to R.string.requerido
    val value = toDoubleOrNull() ?: return null to R.string.numero_invalido
    return value.toFloat() to null
}

fun String.validateOdometer(lastOdometer: Int): Pair<Int?, Int?> {
    if (isBlank()) return null to R.string.requerido
    val value = toDoubleOrNull() ?: return null to R.string.numero_invalido
    if (value.toInt() < lastOdometer) return null to R.string.valor_invalido
    return value.toInt() to null
}

private const val KM_TO_MILES = 0.621371
private const val MILES_TO_KM = 1.60934

fun kmToMiles(km: Double): Double =
    km * KM_TO_MILES

fun milesToKm(miles: Double): Double =
    miles * MILES_TO_KM


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

fun getRequiredPermissions(): Array<String> {
    val permissions = mutableListOf<String>()

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        // Android 12+
        permissions.add(Manifest.permission.BLUETOOTH_SCAN)
        permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
    } else {
        // Android 11 o menor
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        // Android 13+
        permissions.add(Manifest.permission.POST_NOTIFICATIONS)
    }

    return permissions.toTypedArray()
}

fun arePermissionsGranted(context: Context, permissions: Array<String>): Boolean {
    return permissions.all { perm ->
        ContextCompat.checkSelfPermission(context, perm) == PackageManager.PERMISSION_GRANTED
    }
}

fun isServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
    val manager = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
    return manager.getRunningServices(Int.MAX_VALUE).any {
        it.service.className == serviceClass.name
    }
}
