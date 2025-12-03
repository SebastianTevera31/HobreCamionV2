package com.rfz.appflotal.presentation.ui.retreatedesign.viewmodel

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfz.appflotal.R
import com.rfz.appflotal.data.model.CatalogItem
import com.rfz.appflotal.data.model.retreaddesing.dto.RetreadDesignCrudDto
import com.rfz.appflotal.data.model.utilization.UtilizationItem
import com.rfz.appflotal.domain.retreadbrand.RetreadBrandListUseCase
import com.rfz.appflotal.domain.retreaddesign.RetreadDesign
import com.rfz.appflotal.domain.retreaddesign.RetreadDesignCrudUseCase
import com.rfz.appflotal.domain.retreaddesign.RetreadDesignListUseCase
import com.rfz.appflotal.domain.utilization.UtilizationUseCase
import com.rfz.appflotal.presentation.ui.commonscreens.listmanager.viewmodel.ListManagementUiState
import com.rfz.appflotal.presentation.ui.commonscreens.listmanager.viewmodel.ListManagementViewModel
import com.rfz.appflotal.presentation.ui.commonscreens.listmanager.viewmodel.ShowToast
import com.rfz.appflotal.presentation.ui.utils.responseHelperWithResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class UploadingDesignItemMessage(@StringRes val message: Int) {
    SUCCESS(R.string.guardado_retreated_design_exito),
    GENERAL_ERROR(R.string.error_guardar_retreated_design),
}

enum class RetreadDesignFields { ID, DESCRIPTION, PROFUNDIDAD_PISO }
enum class RetreadCatalogDesignFields { MARCA_RENOVADO, UTILIZACION }

data class RetreadDesignDialogState(
    val id: String = "0",
    val description: String = "",
    val marcaRenovado: CatalogItem? = null,
    val profundidadPiso: String = "",
    val utilizacion: CatalogItem? = null,
)

@HiltViewModel
class RetreatedDesignViewModel @Inject constructor(
    private val retreadDesignCrudUseCase: RetreadDesignCrudUseCase,
    private val retreadDesignListUseCase: RetreadDesignListUseCase,
    private val retreadBrandListUseCase: RetreadBrandListUseCase,
    private val utilizationUseCase: UtilizationUseCase
) : ViewModel(), ListManagementViewModel<RetreadDesign> {

    private var _uiState = MutableStateFlow(ListManagementUiState<RetreadDesign>())
    override val uiState: StateFlow<ListManagementUiState<RetreadDesign>> = _uiState.asStateFlow()

    private var _dialogState = MutableStateFlow(RetreadDesignDialogState())
    val dialogState: StateFlow<RetreadDesignDialogState> = _dialogState.asStateFlow()

    private var _eventFlow = MutableSharedFlow<ShowToast>()
    val eventFlow = _eventFlow.asSharedFlow()

    // Estas listas actúan como un caché para los catálogos
    var brandList: List<CatalogItem> = emptyList()
        private set
    var utilizationList: List<CatalogItem> = emptyList()
        private set

    init {
        viewModelScope.launch {
            val brandJob = launch {
                responseHelperWithResult(retreadBrandListUseCase.invoke()) { response ->
                    brandList = response.map { it.toDomain() }
                }
            }
            val utilizationJob = launch {
                responseHelperWithResult(utilizationUseCase.invoke()) { response ->
                    utilizationList = response.map { it.toDomain() }
                }
            }

            joinAll(brandJob, utilizationJob)
            loadItems()
        }
    }

    override fun loadItems() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val designListResult = retreadDesignListUseCase.invoke()
            responseHelperWithResult(designListResult) { designDtoList ->
                val itemList = designDtoList.map { it.toDomain() }
                _uiState.update { currentUiState ->
                    currentUiState.copy(
                        originalItems = itemList,
                        itemsToShow = itemList,
                        isLoading = false
                    )
                }
            }
        }
    }

    override fun setTitle(title: String) {
        _uiState.update { currentUiState ->
            currentUiState.copy(
                title = title
            )
        }
    }

    override fun onSearchQueryChanged(query: String) {
        _uiState.update { currentState ->
            val filteredList = if (query.isBlank()) {
                currentState.originalItems
            } else {
                currentState.originalItems.filter { design ->
                    design.description.contains(query.trim(), ignoreCase = true)
                            || design.retreadBrand.contains(query.trim(), ignoreCase = true)
                            || design.utilization.contains(query.trim(), ignoreCase = true)
                            || design.treadDepth == query.trim().toIntOrNull()
                }
            }
            currentState.copy(
                searchQuery = query,
                itemsToShow = filteredList
            )
        }
    }

    override fun onClearQuery() {
        _uiState.update { currentUiState ->
            currentUiState.copy(
                searchQuery = "",
                itemsToShow = currentUiState.originalItems
            )
        }
    }

    override fun onShowDialog() {
        _uiState.update { currentUiState ->
            currentUiState.copy(
                showDialog = true
            )
        }
    }

    override fun onDismissDialog() {
        _uiState.update { currentUiState ->
            currentUiState.copy(
                showDialog = false,
            )
        }
        // Resetea el estado del diálogo
        _dialogState.value = RetreadDesignDialogState()
    }

    override fun onEditing(isEditing: Boolean) {
        _uiState.update { currentUiState ->
            currentUiState.copy(
                isEditing = isEditing
            )
        }
    }

    override fun onSaveItem() {
        _uiState.update { it.copy(isSending = true) }
        viewModelScope.launch {
            val data = _dialogState.value

            // Usa toIntOrNull para un parseo más seguro
            val response = retreadDesignCrudUseCase.invoke(
                RetreadDesignCrudDto(
                    idRetreadDesign = data.id.toIntOrNull() ?: 0,
                    description = data.description,
                    retreadBrandId = data.marcaRenovado?.id ?: 0,
                    utilizationId = data.utilizacion?.id ?: 0,
                    treadDepth = data.profundidadPiso.toIntOrNull() ?: 0
                )
            )

            _uiState.update { it.copy(isSending = false) }

            if (response.isSuccess) {
                _eventFlow.emit(ShowToast(UploadingDesignItemMessage.SUCCESS.message))
                loadItems() // Recarga los datos para mostrar los cambios
            } else {
                _eventFlow.emit(ShowToast(UploadingDesignItemMessage.GENERAL_ERROR.message))
            }

            onDismissDialog()
        }
    }

    fun onDialogFieldChanged(field: RetreadDesignFields, value: String) {
        _dialogState.update { currentState ->
            when (field) {
                RetreadDesignFields.ID -> currentState.copy(id = value)
                RetreadDesignFields.DESCRIPTION -> currentState.copy(description = value)
                RetreadDesignFields.PROFUNDIDAD_PISO -> currentState.copy(profundidadPiso = value)
            }
        }
    }

    fun onDialogCatalogFieldChanged(
        field: RetreadCatalogDesignFields,
        value: CatalogItem
    ) {
        _dialogState.update { currentState ->
            when (field) {
                RetreadCatalogDesignFields.MARCA_RENOVADO -> currentState.copy(marcaRenovado = value)
                RetreadCatalogDesignFields.UTILIZACION -> currentState.copy(utilizacion = value)
            }
        }
    }

    fun setItemToDialog(item: RetreadDesign) {
        onEditing(true)
        _dialogState.update {
            it.copy(
                id = item.idDesign.toString().trim(),
                description = item.description.trim(),
                marcaRenovado = UtilizationItem(item.idRetreadBrand, item.retreadBrand),
                profundidadPiso = item.treadDepth.toString().trim(),
                utilizacion = UtilizationItem(item.idUtilization, item.utilization)
            )
        }
    }
}
