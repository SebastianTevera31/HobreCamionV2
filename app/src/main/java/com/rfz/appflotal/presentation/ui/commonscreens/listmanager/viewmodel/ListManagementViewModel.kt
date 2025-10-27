package com.rfz.appflotal.presentation.ui.commonscreens.listmanager.viewmodel

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
     * Se llama cuando el usuario establece el título de la pantalla.
     */
    fun setTitle(title: String)

    /**
     * Se llama cuando el usuario modifica el texto en la barra de búsqueda.
     */
    fun onSearchQueryChanged(query: String)

    /**
     * Se llama cuando el usuario borra el texto en la barra de búsqueda.
     */
    fun onClearQuery()

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
    fun onAddItem()

    /**
     * Se llama cuando el usuario confirma la acción de edición en el diálogo.
     */
    fun onUpdateItem()

    /**
     * Se llama cuando el usuario quiere editar un ítem de la lista.
     */
    fun onEditing(isEditing: Boolean)
}

/**
 * Representa el estado completo y genérico de una pantalla de gestión.
 *
 * @param T El tipo de dato de los ítems.
 */
data class ListManagementUiState<T>(
    val title: String = "",
    val searchQuery: String = "",
    val originalItems: List<T> = emptyList(),
    val itemsToShow: List<T> = emptyList(),
    val isLoading: Boolean = true,
    val isEditing: Boolean = false,
    val isSending: Boolean = false,
    val showDialog: Boolean = false,
)
