package com.rfz.appflotal.presentation.ui.permission

import android.content.Context
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.data.network.service.HombreCamionService
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.inicio.ui.arePermissionsGranted
import com.rfz.appflotal.presentation.ui.inicio.ui.getRequiredPermissions
import com.rfz.appflotal.presentation.ui.inicio.ui.isServiceRunning

@Composable
fun PermissionScreen(
    context: Context,
    launcher: ManagedActivityResultLauncher<Array<String>, Map<String, @JvmSuppressWildcards Boolean>>,
    allGranted: Boolean,
    modifier: Modifier = Modifier,
    onGranted: () -> Unit
) {
    val permissions = getRequiredPermissions()
    // Efecto: si ya están concedidos, arrancar servicio automáticamente
    LaunchedEffect(Unit) {
        if (arePermissionsGranted(context, permissions)) {
            if (!isServiceRunning(context, HombreCamionService::class.java)) {
                HombreCamionService.startService(context)
            }
            onGranted()
        }
    }

    LaunchedEffect(allGranted) {
        if (allGranted) onGranted()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Necesitamos permisos para funcionar correctamente")
        Spacer(modifier = Modifier.height(16.dp))
        Text("La app requiere acceso a Bluetooth y Notificaciones para conectarse con tu monitor TPMS Hawkhead.")
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = {
            launcher.launch(permissions)
        }) {
            Text("Conceder permisos")
        }
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun PermissionScreenPreview() {
    HombreCamionTheme {
        val ctx = LocalContext.current
        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) { result ->
            val todosConcedidos = result.values.all { it }
            if (todosConcedidos) {
                true
            } else {
                Log.d("Permiso", "❌ Permiso denegado")
            }
        }
        PermissionScreen(
            context = ctx,
            allGranted = false,
            launcher = permissionLauncher,
            modifier = Modifier.safeContentPadding()
        ) {}
    }
}