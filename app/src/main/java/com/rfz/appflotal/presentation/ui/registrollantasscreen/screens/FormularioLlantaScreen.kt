package com.rfz.appflotal.presentation.ui.registrollantasscreen.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rfz.appflotal.R
import com.rfz.appflotal.presentation.commons.SimpleTopBar
import com.rfz.appflotal.presentation.theme.Dimens
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.registrollantasscreen.components.AvisoDatosBloqueados
import com.rfz.appflotal.presentation.ui.registrollantasscreen.components.AvisoSinProductos
import com.rfz.appflotal.presentation.ui.registrollantasscreen.components.CampoFechaAdquisicion
import com.rfz.appflotal.presentation.ui.registrollantasscreen.components.CampoProducto
import com.rfz.appflotal.presentation.ui.registrollantasscreen.components.CampoTextoLlanta
import com.rfz.appflotal.presentation.ui.registrollantasscreen.components.EstadoErrorCarga
import com.rfz.appflotal.presentation.ui.registrollantasscreen.components.HojaSeleccionProducto
import com.rfz.appflotal.presentation.ui.registrollantasscreen.components.SelectorTipoAdquisicion
import com.rfz.appflotal.presentation.ui.registrollantasscreen.components.TituloSeccion
import com.rfz.appflotal.presentation.ui.registrollantasscreen.model.CampoLlanta
import com.rfz.appflotal.presentation.ui.registrollantasscreen.model.CamposLlanta
import com.rfz.appflotal.presentation.ui.registrollantasscreen.model.ErrorCampo
import com.rfz.appflotal.presentation.ui.registrollantasscreen.model.ProductoUi
import com.rfz.appflotal.presentation.ui.registrollantasscreen.model.TipoAdquisicionUi
import com.rfz.appflotal.presentation.ui.registrollantasscreen.model.ValidadorFormularioLlanta
import com.rfz.appflotal.presentation.ui.registrollantasscreen.viewmodel.EstadoCarga
import com.rfz.appflotal.presentation.ui.registrollantasscreen.viewmodel.EventoFormularioLlanta
import com.rfz.appflotal.presentation.ui.registrollantasscreen.viewmodel.FormularioLlantaUiState
import com.rfz.appflotal.presentation.ui.registrollantasscreen.viewmodel.FormularioLlantaViewModel
import kotlinx.coroutines.launch

data class AccionesFormularioLlanta(
    val onBackClick: () -> Unit = {},
    val onTipoAdquisicion: (TipoAdquisicionUi) -> Unit = {},
    val onProducto: (ProductoUi) -> Unit = {},
    val onFecha: (String) -> Unit = {},
    val onCosto: (String) -> Unit = {},
    val onFolio: (String) -> Unit = {},
    val onProfundidad: (String) -> Unit = {},
    val onNumeroLlanta: (String) -> Unit = {},
    val onDot: (String) -> Unit = {},
    val onGuardar: () -> Unit = {},
    val onReintentar: () -> Unit = {},
    val onCrearProducto: () -> Unit = {},
)

@Composable
fun FormularioLlantaRoute(
    onBackClick: () -> Unit,
    onGuardada: () -> Unit,
    onCrearProducto: () -> Unit,
    viewModel: FormularioLlantaViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val mensajeIncompletos = stringResource(R.string.rl_campos_incompletos)
    val mensajeErrorGuardar = stringResource(R.string.rl_error_guardar)

    // Al volver de crear un producto, el formulario recompone y pide el catálogo otra vez.
    LaunchedEffect(Unit) { viewModel.recargarProductosSiFaltan() }

    LaunchedEffect(viewModel) {
        viewModel.eventos.collect { evento ->
            val mensaje = when (evento) {
                EventoFormularioLlanta.Guardada -> {
                    onGuardada()
                    return@collect
                }
                EventoFormularioLlanta.CamposIncompletos -> mensajeIncompletos
                is EventoFormularioLlanta.ErrorAlGuardar -> evento.mensaje ?: mensajeErrorGuardar
            }
            // Sin esperar al snackbar: un guardado posterior debe navegar de inmediato.
            snackbarHostState.currentSnackbarData?.dismiss()
            launch { snackbarHostState.showSnackbar(mensaje) }
        }
    }

    FormularioLlantaContent(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        acciones = AccionesFormularioLlanta(
            onBackClick = onBackClick,
            onTipoAdquisicion = viewModel::onTipoAdquisicionSeleccionado,
            onProducto = viewModel::onProductoSeleccionado,
            onFecha = viewModel::onFechaSeleccionada,
            onCosto = viewModel::onCostoCambiado,
            onFolio = viewModel::onFolioCambiado,
            onProfundidad = viewModel::onProfundidadCambiada,
            onNumeroLlanta = viewModel::onNumeroLlantaCambiado,
            onDot = viewModel::onDotCambiado,
            onGuardar = viewModel::onGuardar,
            onReintentar = viewModel::onReintentar,
            onCrearProducto = onCrearProducto,
        )
    )
}

@Composable
fun FormularioLlantaContent(
    uiState: FormularioLlantaUiState,
    acciones: AccionesFormularioLlanta,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    var confirmarSalida by rememberSaveable { mutableStateOf(false) }
    var mostrarProductos by rememberSaveable { mutableStateOf(false) }

    // Mientras se guarda no se sale; con cambios se pregunta antes de perderlos.
    val intentarSalir: () -> Unit = {
        if (uiState.hayCambios) {
            if (!uiState.guardando) confirmarSalida = true
        } else if (!uiState.guardando) {
            acciones.onBackClick()
        }
    }
    BackHandler(enabled = uiState.guardando || uiState.hayCambios) { intentarSalir() }

    val titulo = stringResource(if (uiState.esEdicion) R.string.editar_llanta else R.string.nueva_llanta)

    Scaffold(
        topBar = {
            Column {
                SimpleTopBar(title = titulo, onBack = intentarSalir)
                if (uiState.estadoCarga == EstadoCarga.CARGANDO || uiState.guardando) {
                    LinearProgressIndicator(Modifier.fillMaxWidth())
                }
            }
        },
        bottomBar = {
            if (uiState.estadoCarga != EstadoCarga.ERROR) {
                BarraGuardar(
                    guardando = uiState.guardando,
                    habilitado = uiState.camposHabilitados,
                    onGuardar = acciones.onGuardar
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
    ) { padding ->
        if (uiState.estadoCarga == EstadoCarga.ERROR) {
            EstadoErrorCarga(
                mensaje = stringResource(R.string.rl_error_cargar_formulario),
                onReintentar = acciones.onReintentar,
                modifier = Modifier.padding(padding)
            )
        } else {
            CamposFormulario(
                uiState = uiState,
                acciones = acciones,
                onAbrirProductos = { mostrarProductos = true },
                modifier = Modifier.padding(padding)
            )
        }
    }

    if (mostrarProductos) {
        HojaSeleccionProducto(
            productos = uiState.productos,
            seleccionado = uiState.campos.producto,
            onSeleccion = acciones.onProducto,
            onCerrar = { mostrarProductos = false }
        )
    }

    if (confirmarSalida) {
        AlertDialog(
            onDismissRequest = { confirmarSalida = false },
            title = { Text(stringResource(R.string.rl_descartar_titulo)) },
            text = { Text(stringResource(R.string.rl_descartar_mensaje)) },
            confirmButton = {
                TextButton(onClick = {
                    confirmarSalida = false
                    acciones.onBackClick()
                }) { Text(stringResource(R.string.rl_descartar)) }
            },
            dismissButton = {
                TextButton(onClick = { confirmarSalida = false }) {
                    Text(stringResource(R.string.rl_seguir_editando))
                }
            }
        )
    }
}

@Composable
private fun CamposFormulario(
    uiState: FormularioLlantaUiState,
    acciones: AccionesFormularioLlanta,
    onAbrirProductos: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val campos = uiState.campos
    val errores = uiState.errores
    val listo = uiState.estadoCarga == EstadoCarga.LISTO
    val adquisicionHabilitada = uiState.camposHabilitados && uiState.adquisicionEditable

    Column(
        verticalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall),
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(Dimens.PaddingMedium)
    ) {
        if (listo && !uiState.adquisicionEditable) AvisoDatosBloqueados()
        if (listo && uiState.adquisicionEditable && uiState.productos.isEmpty()) {
            AvisoSinProductos(onCrearProducto = acciones.onCrearProducto)
        }

        TituloSeccion(stringResource(R.string.rl_seccion_producto))
        CampoProducto(
            producto = campos.producto,
            onClick = onAbrirProductos,
            error = errores[CampoLlanta.PRODUCTO],
            habilitado = adquisicionHabilitada && uiState.productos.isNotEmpty()
        )
        CampoTextoLlanta(
            etiqueta = stringResource(R.string.profundidad),
            valor = campos.profundidad,
            onValorCambiado = acciones.onProfundidad,
            error = errores[CampoLlanta.PROFUNDIDAD],
            habilitado = adquisicionHabilitada,
            obligatorio = true,
            numerico = true
        )

        TituloSeccion(stringResource(R.string.rl_seccion_adquisicion))
        SelectorTipoAdquisicion(
            tipos = uiState.tiposAdquisicion,
            seleccionado = campos.tipoAdquisicion,
            onSeleccion = acciones.onTipoAdquisicion,
            error = errores[CampoLlanta.TIPO_ADQUISICION],
            habilitado = adquisicionHabilitada
        )
        CampoFechaAdquisicion(
            fechaIso = campos.fechaAdquisicion,
            onFechaSeleccionada = acciones.onFecha,
            error = errores[CampoLlanta.FECHA],
            habilitado = adquisicionHabilitada
        )
        CampoTextoLlanta(
            etiqueta = stringResource(R.string.costo),
            valor = campos.costo,
            onValorCambiado = acciones.onCosto,
            error = errores[CampoLlanta.COSTO],
            habilitado = adquisicionHabilitada,
            obligatorio = true,
            numerico = true
        )
        CampoTextoLlanta(
            etiqueta = stringResource(R.string.folio_factura),
            valor = campos.folioFactura,
            onValorCambiado = acciones.onFolio,
            habilitado = uiState.camposHabilitados
        )

        TituloSeccion(stringResource(R.string.rl_seccion_identificacion))
        CampoTextoLlanta(
            etiqueta = stringResource(R.string.numero_de_llanta),
            valor = campos.numeroLlanta,
            onValorCambiado = acciones.onNumeroLlanta,
            error = errores[CampoLlanta.NUMERO_LLANTA],
            habilitado = uiState.camposHabilitados,
            obligatorio = true,
            maximo = ValidadorFormularioLlanta.MAX_NUMERO_LLANTA
        )
        CampoTextoLlanta(
            etiqueta = stringResource(R.string.dot),
            valor = campos.dot,
            onValorCambiado = acciones.onDot,
            error = errores[CampoLlanta.DOT],
            habilitado = uiState.camposHabilitados,
            maximo = ValidadorFormularioLlanta.MAX_DOT,
            imeAction = ImeAction.Done
        )
    }
}

@Composable
private fun BarraGuardar(guardando: Boolean, habilitado: Boolean, onGuardar: () -> Unit) {
    Surface(tonalElevation = 3.dp, shadowElevation = 8.dp) {
        Box(Modifier.windowInsetsPadding(WindowInsets.navigationBars.union(WindowInsets.ime))) {
            Button(
                onClick = onGuardar,
                enabled = habilitado,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.PaddingMedium, vertical = Dimens.PaddingSmall)
            ) {
                if (guardando) {
                    CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
                } else {
                    Text(stringResource(R.string.rl_guardar_llanta))
                }
            }
        }
    }
}

private val productosEjemplo = listOf(ProductoUi(1, "Michelin X Multi Z 295/80R22.5", 16))
private val tiposEjemplo = listOf(TipoAdquisicionUi(1, "Compra", "Purchase"))

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun FormularioLlantaNuevaPreview() {
    HombreCamionTheme {
        FormularioLlantaContent(
            uiState = FormularioLlantaUiState(
                estadoCarga = EstadoCarga.LISTO,
                tiposAdquisicion = tiposEjemplo,
                productos = productosEjemplo,
                campos = CamposLlanta(fechaAdquisicion = "2026-11-27", costo = "4500"),
                errores = mapOf(CampoLlanta.NUMERO_LLANTA to ErrorCampo.REQUERIDO)
            ),
            acciones = AccionesFormularioLlanta()
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun FormularioLlantaBloqueadaPreview() {
    HombreCamionTheme {
        FormularioLlantaContent(
            uiState = FormularioLlantaUiState(
                esEdicion = true,
                estadoCarga = EstadoCarga.LISTO,
                productos = productosEjemplo,
                adquisicionEditable = false,
                campos = CamposLlanta(
                    tipoAdquisicion = tiposEjemplo[0],
                    producto = productosEjemplo[0],
                    fechaAdquisicion = "2026-03-01T00:00:00",
                    costo = "4500",
                    profundidad = "16",
                    numeroLlanta = "A-1023",
                    dot = "2423"
                )
            ),
            acciones = AccionesFormularioLlanta()
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun FormularioLlantaErrorPreview() {
    HombreCamionTheme {
        FormularioLlantaContent(
            uiState = FormularioLlantaUiState(estadoCarga = EstadoCarga.ERROR),
            acciones = AccionesFormularioLlanta()
        )
    }
}
