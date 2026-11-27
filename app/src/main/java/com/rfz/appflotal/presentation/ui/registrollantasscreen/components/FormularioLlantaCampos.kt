package com.rfz.appflotal.presentation.ui.registrollantasscreen.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.R
import com.rfz.appflotal.core.util.AppLocale
import com.rfz.appflotal.core.util.Commons
import com.rfz.appflotal.presentation.theme.Dimens
import com.rfz.appflotal.presentation.ui.languaje.LocalizedApp
import com.rfz.appflotal.presentation.ui.registrollantasscreen.model.ErrorCampo
import com.rfz.appflotal.presentation.ui.registrollantasscreen.model.ProductoUi
import com.rfz.appflotal.presentation.ui.registrollantasscreen.model.TipoAdquisicionUi
import com.rfz.appflotal.presentation.ui.utils.datePickerMillisToIsoDate
import com.rfz.appflotal.presentation.ui.utils.isoDateToDatePickerMillis
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val FormaCampo = RoundedCornerShape(12.dp)

@Composable
fun TituloSeccion(texto: String, modifier: Modifier = Modifier) {
    Text(
        text = texto.uppercase(),
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier.padding(top = Dimens.PaddingSmall)
    )
}

@Composable
fun ErrorCampo.texto(maximo: Int? = null): String = when (this) {
    ErrorCampo.REQUERIDO -> stringResource(R.string.rl_campo_obligatorio)
    ErrorCampo.MAYOR_A_CERO -> stringResource(R.string.rl_mayor_a_cero)
    ErrorCampo.EXCEDE_LONGITUD -> stringResource(R.string.rl_maximo_caracteres, maximo ?: 0)
}

private fun etiqueta(texto: String, obligatorio: Boolean) = if (obligatorio) "$texto *" else texto

@Composable
fun CampoTextoLlanta(
    etiqueta: String,
    valor: String,
    onValorCambiado: (String) -> Unit,
    modifier: Modifier = Modifier,
    error: ErrorCampo? = null,
    habilitado: Boolean = true,
    obligatorio: Boolean = false,
    numerico: Boolean = false,
    maximo: Int? = null,
    imeAction: ImeAction = ImeAction.Next,
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onValorCambiado,
        enabled = habilitado,
        singleLine = true,
        isError = error != null,
        label = { Text(etiqueta(etiqueta, obligatorio)) },
        supportingText = if (error != null || maximo != null) {
            {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(error?.texto(maximo).orEmpty())
                    if (maximo != null) Text("${valor.length}/$maximo")
                }
            }
        } else null,
        keyboardOptions = KeyboardOptions(
            keyboardType = if (numerico) KeyboardType.Decimal else KeyboardType.Text,
            imeAction = imeAction
        ),
        shape = FormaCampo,
        modifier = modifier.fillMaxWidth()
    )
}

/** Campo de solo lectura que abre un selector al tocarlo. */
@Composable
private fun CampoSelector(
    etiqueta: String,
    valor: String,
    icono: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    error: ErrorCampo? = null,
    habilitado: Boolean = true,
    obligatorio: Boolean = false,
) {
    Box(modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = valor,
            onValueChange = {},
            readOnly = true,
            enabled = habilitado,
            singleLine = true,
            isError = error != null,
            label = { Text(etiqueta(etiqueta, obligatorio)) },
            placeholder = { Text(placeholder) },
            trailingIcon = { Icon(icono, contentDescription = null) },
            supportingText = error?.let { { Text(it.texto()) } },
            shape = FormaCampo,
            modifier = Modifier.fillMaxWidth()
        )
        // El TextField de solo lectura no emite clics: una capa encima los recibe.
        if (habilitado) {
            Box(
                Modifier
                    .matchParentSize()
                    .padding(top = 8.dp)
                    .clickable(onClick = onClick)
            )
        }
    }
}

@Composable
fun CampoProducto(
    producto: ProductoUi?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    error: ErrorCampo? = null,
    habilitado: Boolean = true,
) {
    CampoSelector(
        etiqueta = stringResource(R.string.rl_producto),
        valor = producto?.descripcion.orEmpty(),
        placeholder = stringResource(R.string.rl_seleccionar_producto),
        icono = Icons.AutoMirrored.Filled.KeyboardArrowRight,
        onClick = onClick,
        error = error,
        habilitado = habilitado,
        obligatorio = true,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectorTipoAdquisicion(
    tipos: List<TipoAdquisicionUi>,
    seleccionado: TipoAdquisicionUi?,
    onSeleccion: (TipoAdquisicionUi) -> Unit,
    modifier: Modifier = Modifier,
    error: ErrorCampo? = null,
    habilitado: Boolean = true,
) {
    var expandido by remember { mutableStateOf(false) }
    val esEspanol = AppLocale.currentLocale.collectAsState().value.language == "es"

    ExposedDropdownMenuBox(
        expanded = expandido && habilitado,
        onExpandedChange = { if (habilitado) expandido = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = seleccionado?.descripcion(esEspanol).orEmpty(),
            onValueChange = {},
            readOnly = true,
            enabled = habilitado,
            singleLine = true,
            isError = error != null,
            label = { Text(etiqueta(stringResource(R.string.tipo_de_adquisici_n), true)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido) },
            supportingText = error?.let { { Text(it.texto()) } },
            shape = FormaCampo,
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
        )
        ExposedDropdownMenu(expanded = expandido && habilitado, onDismissRequest = { expandido = false }) {
            tipos.forEach { tipo ->
                DropdownMenuItem(
                    text = { Text(tipo.descripcion(esEspanol)) },
                    onClick = {
                        onSeleccion(tipo)
                        expandido = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampoFechaAdquisicion(
    fechaIso: String,
    onFechaSeleccionada: (String) -> Unit,
    modifier: Modifier = Modifier,
    error: ErrorCampo? = null,
    habilitado: Boolean = true,
) {
    var mostrarSelector by remember { mutableStateOf(false) }

    CampoSelector(
        etiqueta = stringResource(R.string.fecha_de_adquisicion),
        valor = fechaParaMostrar(fechaIso),
        icono = Icons.Default.DateRange,
        onClick = { mostrarSelector = true },
        error = error,
        habilitado = habilitado,
        obligatorio = true,
        modifier = modifier
    )

    if (mostrarSelector) {
        LocalizedApp {
            // El DatePicker trabaja en medianoche UTC; ver DatePickerDates.
            val estado = rememberDatePickerState(
                initialSelectedDateMillis = isoDateToDatePickerMillis(fechaIso)
            )
            DatePickerDialog(
                onDismissRequest = { mostrarSelector = false },
                confirmButton = {
                    TextButton(onClick = {
                        estado.selectedDateMillis?.let { onFechaSeleccionada(datePickerMillisToIsoDate(it)) }
                        mostrarSelector = false
                    }) { Text(stringResource(R.string.guardar)) }
                },
                dismissButton = {
                    TextButton(onClick = { mostrarSelector = false }) {
                        Text(stringResource(R.string.cancelar))
                    }
                }
            ) {
                DatePicker(state = estado)
            }
        }
    }
}

private fun fechaParaMostrar(fechaIso: String): String = try {
    LocalDate.parse(fechaIso.trim().substringBefore('T'), DateTimeFormatter.ISO_LOCAL_DATE)
        .format(DateTimeFormatter.ofPattern(Commons.displayDatePattern()))
} catch (_: Exception) {
    ""
}

@Composable
fun AvisoDatosBloqueados(modifier: Modifier = Modifier) {
    AvisoFormulario(
        icono = Icons.Outlined.Info,
        texto = stringResource(R.string.editar_llanta_datos_bloqueados),
        modifier = modifier
    )
}

@Composable
fun AvisoSinProductos(onCrearProducto: () -> Unit, modifier: Modifier = Modifier) {
    AvisoFormulario(
        icono = Icons.Outlined.Inventory2,
        texto = stringResource(R.string.rl_sin_productos),
        accion = stringResource(R.string.rl_crear_producto),
        onAccion = onCrearProducto,
        modifier = modifier
    )
}

@Composable
private fun AvisoFormulario(
    icono: ImageVector,
    texto: String,
    modifier: Modifier = Modifier,
    accion: String? = null,
    onAccion: () -> Unit = {},
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall),
            modifier = Modifier.padding(Dimens.PaddingMedium)
        ) {
            Icon(icono, contentDescription = null)
            Column {
                Text(texto, style = MaterialTheme.typography.bodyMedium)
                if (accion != null) {
                    TextButton(onClick = onAccion) { Text(accion) }
                }
            }
        }
    }
}
