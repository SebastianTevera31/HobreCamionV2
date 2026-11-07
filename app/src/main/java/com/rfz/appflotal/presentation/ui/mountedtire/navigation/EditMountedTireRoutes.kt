package com.rfz.appflotal.presentation.ui.mountedtire.navigation

import kotlinx.serialization.Serializable

/**
 * Ruta type-safe para editar la ficha de la llanta montada en [position].
 * Solo modifica la llanta (api/Tire/CrudTire); no desmonta ni cambia el montaje.
 */
@Serializable
data class EditMountedTireRoute(val position: String)
