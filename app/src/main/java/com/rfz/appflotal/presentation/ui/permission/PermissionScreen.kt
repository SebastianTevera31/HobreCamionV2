package com.rfz.appflotal.presentation.ui.permission

import android.content.Context
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.R
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
        verticalArrangement = Arrangement.spacedBy(
            dimensionResource(R.dimen.medium_dimen),
            Alignment.CenterVertically
        ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.permission_image),
            contentDescription = null,
            modifier = Modifier.clip(CircleShape)
        )
        Text(
            stringResource(R.string.permiso_bluetooth_notificaciones),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Text(
            stringResource(R.string.texto_permisos_app),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.small_dimen)))
        Button(
            onClick = {
                launcher.launch(permissions)
            },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.height(60.dp)
        ) {
            Text(stringResource(R.string.conceder_permisos_btn))
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
                Log.d("Permiso", "Permiso denegado")
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