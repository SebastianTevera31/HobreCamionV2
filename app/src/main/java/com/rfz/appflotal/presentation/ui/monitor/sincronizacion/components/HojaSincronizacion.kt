package com.rfz.appflotal.presentation.ui.monitor.sincronizacion.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.R
import com.rfz.appflotal.presentation.ui.languaje.LocalizedApp
import com.rfz.appflotal.presentation.ui.monitor.sincronizacion.model.CargaSincronizacion
import com.rfz.appflotal.presentation.ui.monitor.sincronizacion.model.EstadoFilaSincronizacion
import com.rfz.appflotal.presentation.ui.monitor.sincronizacion.model.EstadoSincronizacionUiState
import com.rfz.appflotal.presentation.ui.monitor.sincronizacion.model.FilaSincronizacionUi
import com.rfz.appflotal.presentation.ui.monitor.sincronizacion.model.IndicadorSincronizacion
import com.rfz.appflotal.presentation.ui.monitor.sincronizacion.model.TipoFilaSincronizacion
import com.rfz.appflotal.presentation.ui.monitor.sincronizacion.viewmodel.EstadoSincronizacionViewModel

/** Hoja, diálogo de descarte y mensajes de la sincronización; se monta junto al Monitor. */
@Composable
fun HojaSincronizacionRoute(
    viewModel: EstadoSincronizacionViewModel,
    uiState: EstadoSincronizacionUiState,
) {
    val context = LocalContext.current
    LaunchedEffect(viewModel) {
        viewModel.efectos.collect { efecto ->
            Toast.makeText(context, efecto.mensaje(), Toast.LENGTH_LONG).show()
        }
    }

    if (uiState.hojaVisible) {
        HojaSincronizacion(
            uiState = uiState,
            onCerrar = viewModel::ocultarHoja,
            onSincronizarAhora = viewModel::sincronizarAhora,
            onReintentar = viewModel::reintentar,
            onDescartar = viewModel::pedirDescartar,
            onRecargar = viewModel::observar,
            onActualizarDatos = viewModel::actualizarDatosOffline
        )
    }
    uiState.porDescartar?.let { fila ->
        DialogoDescartarOperacion(
            fila = fila,
            onConfirmar = viewModel::confirmarDescartar,
            onCancelar = viewModel::cancelarDescartar
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HojaSincronizacion(
    uiState: EstadoSincronizacionUiState,
    onCerrar: () -> Unit,
    onSincronizarAhora: () -> Unit,
    onReintentar: (FilaSincronizacionUi) -> Unit,
    onDescartar: (FilaSincronizacionUi) -> Unit,
    onRecargar: () -> Unit,
    onActualizarDatos: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onCerrar,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        LocalizedApp {
            HojaSincronizacionContent(
                uiState = uiState,
                onSincronizarAhora = onSincronizarAhora,
                onReintentar = onReintentar,
                onDescartar = onDescartar,
                onRecargar = onRecargar,
                onActualizarDatos = onActualizarDatos
            )
        }
    }
}

@Composable
fun HojaSincronizacionContent(
    uiState: EstadoSincronizacionUiState,
    onSincronizarAhora: () -> Unit,
    onReintentar: (FilaSincronizacionUi) -> Unit,
    onDescartar: (FilaSincronizacionUi) -> Unit,
    onRecargar: () -> Unit,
    modifier: Modifier = Modifier,
    onActualizarDatos: () -> Unit = {},
) {
    val hayOperaciones = uiState.enCola.isNotEmpty() || uiState.requierenAtencion.isNotEmpty()
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 24.dp)
    ) {
        Text(stringResource(R.string.sinc_titulo), style = MaterialTheme.typography.titleLarge)
        LineaEstado(uiState)
        Text(
            text = uiState.ultimaSincronizacion
                ?.let { stringResource(R.string.sinc_ultima, formatearMomento(it)) }
                ?: stringResource(R.string.sinc_nunca),
            style = MaterialTheme.typography.bodySmall
        )
        FilledTonalButton(
            onClick = onSincronizarAhora,
            enabled = uiState.conConexion && !uiState.sincronizando && hayOperaciones,
            modifier = Modifier.align(Alignment.End)
        ) {
            Icon(Icons.Filled.Sync, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.size(8.dp))
            Text(stringResource(R.string.sinc_sincronizar_ahora))
        }

        val enviando =
            uiState.sincronizando || uiState.indicador == IndicadorSincronizacion.ENVIANDO
        if (enviando || uiState.carga == CargaSincronizacion.CARGANDO) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        } else {
            Spacer(Modifier.height(4.dp))
        }

        when {
            uiState.carga == CargaSincronizacion.CARGANDO -> repeat(2) { FilaEsqueleto() }
            uiState.carga == CargaSincronizacion.ERROR -> EstadoError(onRecargar)
            !hayOperaciones -> EstadoVacio()
            else -> ListaOperaciones(uiState, onReintentar, onDescartar)
        }
        SeccionDatosOffline(
            datos = uiState.datosOffline,
            conConexion = uiState.conConexion,
            onActualizar = onActualizarDatos
        )
    }
}

@Composable
private fun LineaEstado(uiState: EstadoSincronizacionUiState) {
    val porEnviar = uiState.enCola.size
    val (color, texto) = when {
        uiState.sesionVencida && porEnviar > 0 ->
            RojoSincronizacion to stringResource(R.string.sinc_sesion_vencida)

        !uiState.conConexion -> AmbarSincronizacion to stringResource(R.string.sinc_sin_conexion)
        uiState.indicador == IndicadorSincronizacion.ENVIANDO ->
            Color(0xFF4CAF50) to stringResource(R.string.sinc_enviando)

        else -> Color(0xFF4CAF50) to stringResource(R.string.sinc_con_conexion)
    }
    val detalle = if (porEnviar > 0) " · " + pluralStringResource(
        R.plurals.sinc_por_enviar,
        porEnviar,
        porEnviar
    ) else ""
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.padding(top = 4.dp)
    ) {
        Box(Modifier
            .size(8.dp)
            .background(color, CircleShape))
        Text(texto + detalle, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun ListaOperaciones(
    uiState: EstadoSincronizacionUiState,
    onReintentar: (FilaSincronizacionUi) -> Unit,
    onDescartar: (FilaSincronizacionUi) -> Unit,
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.heightIn(max = 420.dp)
    ) {
        if (uiState.requierenAtencion.isNotEmpty()) {
            item {
                Encabezado(
                    stringResource(
                        R.string.sinc_requieren_atencion,
                        uiState.requierenAtencion.size
                    )
                )
            }
            items(uiState.requierenAtencion, key = { "error-${it.id}" }) { fila ->
                FilaRechazada(
                    fila = fila,
                    habilitada = !uiState.sincronizando,
                    onReintentar = { onReintentar(fila) },
                    onDescartar = { onDescartar(fila) }
                )
            }
        }
        if (uiState.enCola.isNotEmpty()) {
            item { Encabezado(stringResource(R.string.sinc_en_cola, uiState.enCola.size)) }
            item {
                Surface(shape = RoundedCornerShape(12.dp), tonalElevation = 1.dp) {
                    Column { uiState.enCola.forEach { FilaEnCola(it) } }
                }
            }
        }
    }
}

@Composable
private fun Encabezado(texto: String) {
    Text(
        text = texto,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 8.dp)
    )
}

@Composable
private fun EstadoVacio() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp)
    ) {
        Icon(
            Icons.Filled.CloudDone,
            contentDescription = null,
            tint = Color(0xFF4CAF50),
            modifier = Modifier.size(56.dp)
        )
        Text(
            stringResource(R.string.sinc_todo_sincronizado),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
private fun EstadoError(onRecargar: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp)
    ) {
        Text(stringResource(R.string.sinc_error_carga), style = MaterialTheme.typography.bodyMedium)
        TextButton(onClick = onRecargar) { Text(stringResource(R.string.sinc_reintentar)) }
    }
}

@Composable
private fun FilaEsqueleto() {
    Box(
        Modifier
            .padding(top = 12.dp)
            .fillMaxWidth()
            .height(48.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
    )
}

private val filasEjemplo = listOf(
    FilaSincronizacionUi(
        1,
        TipoFilaSincronizacion.DESMONTAJE_CON_INSPECCION,
        "P3",
        0,
        EstadoFilaSincronizacion.ENVIANDO,
        null
    ),
    FilaSincronizacionUi(
        3,
        TipoFilaSincronizacion.MONTAJE,
        "P3",
        0,
        EstadoFilaSincronizacion.PENDIENTE,
        null
    ),
    FilaSincronizacionUi(
        5,
        TipoFilaSincronizacion.MONTAJE,
        "P5",
        0,
        EstadoFilaSincronizacion.EN_ESPERA,
        null
    ),
)

@Preview(showBackground = true)
@Composable
private fun HojaSincronizacionListaPreview() {
    HojaSincronizacionContent(
        uiState = EstadoSincronizacionUiState(
            carga = CargaSincronizacion.LISTA,
            indicador = IndicadorSincronizacion.REQUIERE_ATENCION,
            conConexion = false,
            requierenAtencion = listOf(
                FilaSincronizacionUi(
                    4,
                    TipoFilaSincronizacion.DESMONTAJE,
                    "P5",
                    0,
                    EstadoFilaSincronizacion.CON_ERROR,
                    null
                )
            ),
            enCola = filasEjemplo
        ),
        onSincronizarAhora = {}, onReintentar = {}, onDescartar = {}, onRecargar = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun HojaSincronizacionVaciaPreview() {
    HojaSincronizacionContent(
        uiState = EstadoSincronizacionUiState(
            carga = CargaSincronizacion.LISTA,
            ultimaSincronizacion = 0
        ),
        onSincronizarAhora = {}, onReintentar = {}, onDescartar = {}, onRecargar = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun HojaSincronizacionCargandoErrorPreview() {
    Column {
        HojaSincronizacionContent(
            uiState = EstadoSincronizacionUiState(carga = CargaSincronizacion.CARGANDO),
            onSincronizarAhora = {}, onReintentar = {}, onDescartar = {}, onRecargar = {}
        )
        HojaSincronizacionContent(
            uiState = EstadoSincronizacionUiState(carga = CargaSincronizacion.ERROR),
            onSincronizarAhora = {}, onReintentar = {}, onDescartar = {}, onRecargar = {}
        )
    }
}
