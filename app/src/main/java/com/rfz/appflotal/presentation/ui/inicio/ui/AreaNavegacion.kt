package com.rfz.appflotal.presentation.ui.inicio.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.union
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged

/**
 * Zona del NavHost y, debajo, la barra inferior de Home.
 *
 * La ventana usa adjustResize: el espacio del teclado se reserva aquí una sola vez, así el
 * contenido se encoge sobre él y el imePadding() de cada pantalla ya no vuelve a sumarlo.
 */
@Composable
fun ColumnScope.AreaNavegacion(
    barraInferior: (@Composable () -> Unit)?,
    contenido: @Composable BoxScope.() -> Unit,
) {
    // Alto total de la barra, que ya incluye la barra de navegación del sistema.
    var altoBarraInferior by remember { mutableIntStateOf(0) }

    // Lo que tapa la barra no se repite en las pantallas, y el teclado que cae sobre ella
    // tampoco. navigationBars cubre el primer frame, antes de medir la barra.
    val ocupadoAbajo = if (barraInferior != null) {
        WindowInsets.navigationBars.union(WindowInsets(bottom = altoBarraInferior))
    } else {
        WindowInsets(0, 0, 0, 0)
    }

    Box(
        modifier = Modifier
            .weight(1f)
            .consumeWindowInsets(ocupadoAbajo)
            .imePadding(),
        content = contenido
    )

    if (barraInferior != null) {
        Box(Modifier.onSizeChanged { altoBarraInferior = it.height }) {
            barraInferior()
        }
    }
}
