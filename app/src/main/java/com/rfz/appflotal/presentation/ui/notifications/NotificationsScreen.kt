package com.rfz.appflotal.presentation.ui.notifications

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.rfz.appflotal.R
import com.rfz.appflotal.core.util.AppLocale
import com.rfz.appflotal.data.model.catalog.MaintenanceNotification
import com.rfz.appflotal.presentation.commons.SimpleTopBar
import com.rfz.appflotal.presentation.theme.Dimens
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.components.EmptyDataCard
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.model.asIcon
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.utils.CompletePlanColors.SubtleText
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.utils.CompletePlanColors.TealMid
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun NotificationsRoute(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NotificationsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    NotificationsScreen(
        uiState = uiState,
        onBack = onBack,
        onAcknowledge = viewModel::acknowledge,
        modifier = modifier
    )
}

@Composable
fun NotificationsScreen(
    uiState: NotificationsUiState,
    onBack: () -> Unit,
    onAcknowledge: (id: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            SimpleTopBar(
                title = stringResource(R.string.notificaciones_titulo),
                onBack = onBack,
                showBackButton = true
            )
        }
    ) { innerPadding ->
        when {
            uiState.isLoading -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = TealMid)
            }

            uiState.notifications.isEmpty() -> Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(Dimens.PaddingMedium)
            ) {
                EmptyDataCard(
                    title = R.string.notificaciones_vacio_titulo,
                    message = R.string.notificaciones_vacio_mensaje,
                    icon = Icons.Outlined.NotificationsNone.asIcon()
                )
            }

            else -> LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(Dimens.PaddingMedium),
                verticalArrangement = Arrangement.spacedBy(Dimens.ListItemSpacing)
            ) {
                items(uiState.notifications, key = { it.id }) { notification ->
                    MaintenanceNotificationCard(
                        notification = notification,
                        onAcknowledge = { onAcknowledge(notification.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun MaintenanceNotificationCard(
    notification: MaintenanceNotification,
    onAcknowledge: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(Dimens.CardPadding),
            verticalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall)
        ) {
            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    imageVector = Icons.Outlined.Build,
                    contentDescription = null,
                    tint = TealMid,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.notificacion_mantenimiento_titulo),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.size(4.dp))
                    Text(
                        text = notification.message,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.size(4.dp))
                    Text(
                        text = stringResource(
                            R.string.notificacion_recibida_el,
                            formatNotificationDate(notification.notifiedAt)
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = SubtleText
                    )
                }
            }
            OutlinedButton(
                onClick = onAcknowledge,
                modifier = Modifier.align(Alignment.End),
                shape = RoundedCornerShape(50)
            ) {
                Text(
                    text = stringResource(R.string.marcar_como_enterado),
                    color = TealMid,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

private fun formatNotificationDate(epochMillis: Long): String =
    DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
        .withLocale(AppLocale.currentLocale.value)
        .format(Instant.ofEpochMilli(epochMillis).atZone(ZoneId.systemDefault()))

@Preview(showBackground = true)
@Composable
fun NotificationsScreenPreview() {
    HombreCamionTheme {
        NotificationsScreen(
            uiState = NotificationsUiState(
                isLoading = false,
                notifications = listOf(
                    MaintenanceNotification(
                        id = 1,
                        message = "Debes inspeccionar tu vehículo al menos una vez al mes.",
                        notifiedAt = 1_760_000_000_000
                    )
                )
            ),
            onBack = {},
            onAcknowledge = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationsScreenEmptyPreview() {
    HombreCamionTheme {
        NotificationsScreen(
            uiState = NotificationsUiState(isLoading = false),
            onBack = {},
            onAcknowledge = {}
        )
    }
}
