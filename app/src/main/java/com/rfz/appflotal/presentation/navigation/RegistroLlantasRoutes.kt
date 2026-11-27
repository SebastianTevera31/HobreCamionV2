package com.rfz.appflotal.presentation.navigation

import kotlinx.serialization.Serializable

/**
 * Formulario de llanta.
 * idLlanta == null -> alta (Nueva llanta)
 * idLlanta != null -> edición
 */
@Serializable
data class RutaFormularioLlanta(val idLlanta: Int? = null)

/** Bandera que el formulario deja en la lista al guardar, para recargarla y avisar. */
const val LLANTA_GUARDADA = "llanta_guardada"
