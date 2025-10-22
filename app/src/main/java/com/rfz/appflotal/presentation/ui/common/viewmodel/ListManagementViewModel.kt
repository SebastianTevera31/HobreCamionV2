package com.rfz.appflotal.presentation.ui.common.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Define el contrato que cualquier ViewModel que gestione una pantalla de tipo "Management List"
 * debe implementar. Fuerza una arquitectura consistente a través de diferentes pantallas.
 *
 * @param T El tipo de dato de los ítems que se gestionan en la lista (e.g., OriginalDesign, TireSize).
 */
interface ListManagementViewModel<T> {

    /**
     * El estado de la UI, expuesto como un StateFlow para ser observado por el Composable.
     * Es la única fuente de verdad para la pantalla.
     */
    val uiState: StateFlow<ListManagementUiState<T>>

    /**
     * Se llama al iniciar la pantalla para cargar los datos iniciales.
     */
    fun loadItems()


    /**
     * Se llama cuando el usuario modifica el texto en la barra de búsqueda.
     */
    fun onSearchQueryChanged(query: String)

    /**
     * Se llama cuando el usuario presiona el FAB para mostrar el diálogo de creación/edición.
     */
    fun onShowDialog()

    /**
     * Se llama para ocultar el diálogo.
     */
    fun onDismissDialog()

    /**
     * Se llama cuando el usuario confirma la acción de guardado en el diálogo.
     */
    fun onSaveItem()

    /**
     * Gestiona los cambios en los campos del formulario del diálogo.
     * @param field Un identificador único para el campo (e.g., "name", "code").
     * @param value El nuevo valor del campo.
     */
    fun onDialogFieldChanged(field: String, value: Any)
}

/**
 * Representa el estado completo y genérico de una pantalla de gestión.
 *
 * @param T El tipo de dato de los ítems.
 */
data class ListManagementUiState<T>(
    val title: String = "",
    val searchQuery: String = "",
    val items: List<T> = emptyList(),
    val isLoading: Boolean = true,
    val showDialog: Boolean = false,
    // Un mapa flexible para contener los datos de los campos del diálogo.
    // Permite que cada pantalla defina sus propios campos sin cambiar la estructura.
    val dialogData: Map<String, Any> = emptyMap()
)
