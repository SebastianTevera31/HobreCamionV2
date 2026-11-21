package com.rfz.appflotal.domain.monitor

/** Una posición del diagrama del monitor, con su lugar en la imagen. */
data class LlantaDiagrama(
    val posicion: String,
    val enAlerta: Boolean,
    val montada: Boolean,
    val activa: Boolean,
    val x: Int,
    val y: Int,
)

/** Las posiciones se comparan sin espacios y en mayúsculas: el servidor no siempre es uniforme. */
fun String.normalizarPosicion(): String = trim().uppercase()
