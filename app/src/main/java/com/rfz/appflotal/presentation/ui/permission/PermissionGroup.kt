package com.rfz.appflotal.presentation.ui.permission

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.rfz.appflotal.R

data class PermissionGroup(
    val id: String,
    val titleRes: Int,
    val rationaleRes: Int,
    val androidPermissions: List<String>
)

enum class GroupGrantState { Granted, Denied, PermanentlyDenied }

fun requiredPermissionGroups(): List<PermissionGroup> {
    val groups = mutableListOf<PermissionGroup>()

    groups += PermissionGroup(
        id = "location",
        titleRes = R.string.permiso_grupo_ubicacion_titulo,
        rationaleRes = R.string.permiso_grupo_ubicacion_rationale,
        androidPermissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        groups += PermissionGroup(
            id = "bluetooth",
            titleRes = R.string.permiso_grupo_bluetooth_titulo,
            rationaleRes = R.string.permiso_grupo_bluetooth_rationale,
            androidPermissions = listOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT
            )
        )
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        groups += PermissionGroup(
            id = "notifications",
            titleRes = R.string.permiso_grupo_notificaciones_titulo,
            rationaleRes = R.string.permiso_grupo_notificaciones_rationale,
            androidPermissions = listOf(Manifest.permission.POST_NOTIFICATIONS)
        )
    }

    return groups
}

fun PermissionGroup.isGranted(context: Context): Boolean =
    androidPermissions.all { permission ->
        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

fun PermissionGroup.grantState(activity: Activity, wasRequestedBefore: Boolean): GroupGrantState = when {
    isGranted(activity) -> GroupGrantState.Granted
    androidPermissions.any { !ActivityCompat.shouldShowRequestPermissionRationale(activity, it) } && wasRequestedBefore ->
        GroupGrantState.PermanentlyDenied

    else -> GroupGrantState.Denied
}
