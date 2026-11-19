package com.rfz.appflotal.presentation.ui.monitor.sincronizacion.components

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.rfz.appflotal.R
import com.rfz.appflotal.presentation.ui.monitor.component.WarningSnackBanner
import com.rfz.appflotal.presentation.ui.monitor.sincronizacion.model.EstadoSincronizacionUiState
import com.rfz.appflotal.presentation.ui.monitor.sincronizacion.model.IndicadorSincronizacion

/** Aviso sobre el diagrama solo cuando hay que actuar: un rechazo o la sesión vencida. */
@Composable
fun BannerSincronizacion(
    uiState: EstadoSincronizacionUiState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (uiState.indicador != IndicadorSincronizacion.REQUIERE_ATENCION) return
    val rechazos = uiState.requierenAtencion.size
    WarningSnackBanner(
        visible = true,
        message = if (rechazos > 0) pluralStringResource(R.plurals.sinc_banner_rechazos, rechazos, rechazos)
        else stringResource(R.string.sinc_sesion_vencida),
        modifier = modifier.clickable(onClick = onClick)
    )
}
