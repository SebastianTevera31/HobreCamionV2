package com.rfz.appflotal.presentation.ui.permission

import android.app.Activity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.rfz.appflotal.presentation.ui.inicio.components.ObserveOnResume
import com.rfz.appflotal.presentation.ui.utils.getRequiredPermissions
import com.rfz.appflotal.presentation.ui.utils.isServiceRunning

@Composable
fun PermissionScreen(
    activity: Activity,
    launcher: ManagedActivityResultLauncher<Array<String>, Map<String, @JvmSuppressWildcards Boolean>>,
    wasRequestedBefore: () -> Boolean,
    markRequested: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
    onGranted: () -> Unit
) {
    val groups = remember { requiredPermissionGroups() }
    var groupStates by remember {
        mutableStateOf(groups.associateWith { it.grantState(activity, wasRequestedBefore()) })
    }

    fun refreshStates() {
        groupStates = groups.associateWith { it.grantState(activity, wasRequestedBefore()) }
        if (groupStates.values.all { it == GroupGrantState.Granted }) {
            if (!isServiceRunning(activity, HombreCamionService::class.java)) {
                HombreCamionService.startService(activity)
            }
            onGranted()
        }
    }

    // Primera composición: por si ya estaban todos concedidos de antes.
    LaunchedEffect(Unit) { refreshStates() }

    // Corrige el bug de navegación: se re-evalúa el estado real de los permisos
    // cada vez que se vuelve a esta pantalla (p. ej. al regresar de Ajustes del
    // sistema), en vez de depender solo de la respuesta del diálogo nativo.
    ObserveOnResume { refreshStates() }

    val hasPermanentlyDenied = groupStates.values.any { it == GroupGrantState.PermanentlyDenied }
    val hasDenied = groupStates.values.any { it != GroupGrantState.Granted }

    Column(
        modifier = modifier
            .background(Color.White)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.medium_dimen)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.permission_image),
            contentDescription = null,
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
        )
        Text(
            stringResource(R.string.permiso_bluetooth_notificaciones),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = Color.Black
        )
        Text(
            stringResource(R.string.texto_permisos_app),
            textAlign = TextAlign.Center,
            color = Color.Black
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            groups.forEach { group ->
                PermissionGroupRow(group = group, state = groupStates.getValue(group))
            }
        }

        if (hasDenied) {
            Text(
                text = stringResource(
                    if (hasPermanentlyDenied) {
                        R.string.permisos_estado_permanentemente_denegados
                    } else {
                        R.string.permisos_estado_algunos_denegados
                    }
                ),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.small_dimen)))

        Button(
            onClick = {
                markRequested()
                launcher.launch(getRequiredPermissions())
            },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
        ) {
            Text(stringResource(R.string.conceder_permisos_btn))
        }

        if (hasPermanentlyDenied) {
            OutlinedButton(
                onClick = onOpenSettings,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                Text(stringResource(R.string.configuracion))
            }
        }
    }
}

@Composable
private fun PermissionGroupRow(
    group: PermissionGroup,
    state: GroupGrantState,
    modifier: Modifier = Modifier
) {
    val granted = state == GroupGrantState.Granted
    val statusColor = if (granted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                modifier = Modifier.size(36.dp),
                shape = CircleShape,
                color = statusColor.copy(alpha = 0.12f)
            ) {
                Icon(
                    imageVector = if (granted) Icons.Filled.CheckCircle else Icons.Filled.Warning,
                    contentDescription = null,
                    tint = statusColor,
                    modifier = Modifier.padding(8.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(group.titleRes),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (!granted) {
                    Text(
                        text = stringResource(group.rationaleRes),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
private fun PermissionScreenPreview() {
    HombreCamionTheme {
        val groups = remember { requiredPermissionGroups() }
        Column(
            modifier = Modifier
                .background(Color.White)
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            groups.forEach { group ->
                PermissionGroupRow(group = group, state = GroupGrantState.Denied)
            }
        }
    }
}
