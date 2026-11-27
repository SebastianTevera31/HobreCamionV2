package com.rfz.appflotal.presentation.ui.registrollantasscreen.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.R
import com.rfz.appflotal.presentation.theme.Dimens
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.registrollantasscreen.model.ProductoUi

/** Selector de producto con búsqueda: el catálogo puede ser largo para un menú desplegable. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HojaSeleccionProducto(
    productos: List<ProductoUi>,
    seleccionado: ProductoUi?,
    onSeleccion: (ProductoUi) -> Unit,
    onCerrar: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onCerrar,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        ContenidoSeleccionProducto(
            productos = productos,
            seleccionado = seleccionado,
            onSeleccion = {
                onSeleccion(it)
                onCerrar()
            }
        )
    }
}

@Composable
private fun ContenidoSeleccionProducto(
    productos: List<ProductoUi>,
    seleccionado: ProductoUi?,
    onSeleccion: (ProductoUi) -> Unit,
) {
    var busqueda by rememberSaveable { mutableStateOf("") }
    val filtrados = remember(productos, busqueda) {
        productos.filter { it.descripcion.contains(busqueda.trim(), ignoreCase = true) }
    }

    Column(Modifier.imePadding()) {
        Text(
            text = stringResource(R.string.rl_seleccionar_producto),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = Dimens.PaddingMedium)
        )
        OutlinedTextField(
            value = busqueda,
            onValueChange = { busqueda = it },
            singleLine = true,
            placeholder = { Text(stringResource(R.string.rl_buscar_producto)) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (busqueda.isNotEmpty()) {
                    IconButton(onClick = { busqueda = "" }) {
                        Icon(Icons.Default.Close, contentDescription = stringResource(R.string.limpiar))
                    }
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.PaddingMedium)
        )

        if (filtrados.isEmpty()) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens.PaddingLarge)
            ) {
                Text(
                    stringResource(R.string.rl_sin_resultados_producto, busqueda.trim()),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(Modifier.heightIn(max = 480.dp)) {
                items(filtrados, key = { it.id }) { producto ->
                    ListItem(
                        headlineContent = { Text(producto.descripcion) },
                        supportingContent = {
                            Text(stringResource(R.string.rl_profundidad_producto, producto.profundidad))
                        },
                        trailingContent = {
                            if (producto.id == seleccionado?.id) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        },
                        modifier = Modifier.clickable { onSeleccion(producto) }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ContenidoSeleccionProductoPreview() {
    val productos = listOf(
        ProductoUi(1, "Michelin X Multi Z 295/80R22.5", 16),
        ProductoUi(2, "Bridgestone R249 295/80R22.5", 15),
    )
    HombreCamionTheme {
        ContenidoSeleccionProducto(productos = productos, seleccionado = productos[0], onSeleccion = {})
    }
}
