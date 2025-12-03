package com.rfz.appflotal.presentation.ui.registrollantasscreen.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfz.appflotal.data.model.acquisitiontype.response.AcquisitionTypeResponse
import com.rfz.appflotal.data.model.product.response.ProductResponse
import com.rfz.appflotal.data.model.tire.dto.TireCrudDto
import com.rfz.appflotal.data.model.tire.response.TireListResponse
import com.rfz.appflotal.domain.acquisitiontype.AcquisitionTypeUseCase
import com.rfz.appflotal.domain.database.GetTasksUseCase
import com.rfz.appflotal.domain.product.ProductListUseCase
import com.rfz.appflotal.domain.tire.TireCrudUseCase
import com.rfz.appflotal.domain.tire.TireGetUseCase
import com.rfz.appflotal.domain.tire.TireListUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class NuevoRegistroLlantasUiState(
    val tires: List<TireListResponse> = emptyList(),
    val displayedTires: List<TireListResponse> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val searchQuery: String = "",
    val isDialogShown: Boolean = false,
    val isEditing: Boolean = false,
    val isSending: Boolean = false,
    val isLoadingDialogData: Boolean = false,
    // Combo box lists
    val acquisitionTypes: List<AcquisitionTypeResponse> = emptyList(),
    val products: List<ProductResponse> = emptyList(),
    // Dialog fields state
    val dialogState: TireDialogState = TireDialogState()
)

data class TireDialogState(
    val id: Int = 0,
    val selectedAcquisitionType: AcquisitionTypeResponse? = null,
    val selectedProduct: ProductResponse? = null,
    val acquisitionDate: String = "",
    val cost: String = "",
    val folioFactura: String = "",
    val treadDepth: String = "",
    val tireNumber: String = "",
    val dot: String = "",
)

@HiltViewModel
class NuevoRegistroLlantasViewModel @Inject constructor(
    private val tireListUsecase: TireListUsecase,
    private val tireGetUseCase: TireGetUseCase,
    private val tireCrudUseCase: TireCrudUseCase,
    private val acquisitionTypeUseCase: AcquisitionTypeUseCase,
    private val productListUseCase: ProductListUseCase,
    private val getTasksUseCase: GetTasksUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(NuevoRegistroLlantasUiState())
    val uiState: StateFlow<NuevoRegistroLlantasUiState> = _uiState.asStateFlow()

    private var token: String? = null

    fun loadData() {
        viewModelScope.launch {
            token = getTasksUseCase().first().firstOrNull()?.fld_token
            loadTires()
            loadComboData()
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        applyFilter()
    }

    fun onClearQuery() {
        _uiState.update { currentUiState ->
            currentUiState.copy(
                searchQuery = "",
                displayedTires = currentUiState.tires
            )
        }
    }

    fun onAddNewTireClicked() {
        _uiState.update {
            it.copy(
                isDialogShown = true,
                isEditing = false,
                dialogState = TireDialogState(acquisitionDate = LocalDateTime.now().toString())
            )
        }
    }

    fun onEditTireClicked(tire: TireListResponse) {
        _uiState.update { it.copy(isDialogShown = true, isEditing = true) }
        loadTireDetails(tire.idTire)
    }

    fun onDismissDialog() {
        _uiState.update { it.copy(isDialogShown = false) }
    }

    fun onDialogFieldChange(update: (TireDialogState) -> TireDialogState) {
        _uiState.update { it.copy(dialogState = update(it.dialogState)) }
    }

    fun saveTire() {
        _uiState.update { it.copy(isSending = true) }

        val currentState = _uiState.value.dialogState
        if (isDialogStateInvalid(currentState)) {
            _uiState.update { it.copy(errorMessage = "Todos los campos requeridos deben estar completos") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val bearerToken = "Bearer ${token ?: ""}"
            val tireDto = TireCrudDto(
                idTire = if (_uiState.value.isEditing) currentState.id else 0,
                typeAcquisitionId = currentState.selectedAcquisitionType!!.idAcquisitionType,
                providerId = 0,
                productId = currentState.selectedProduct!!.idProduct,
                acquisitionDate = formatAcquisitionDate(currentState.acquisitionDate),
                document = currentState.folioFactura,
                unitCost = currentState.cost.toInt(),
                dot = currentState.dot,
                tireNumber = currentState.tireNumber,
                treadDepth = currentState.treadDepth.toInt(),
                registrationDate = LocalDateTime.now().toString(),
                isActive = true,
                retreadDesignId = 0,
                destination = 2,
                lifecycle = 0,
            )

            try {
                val result = tireCrudUseCase(bearerToken, tireDto)
                if (result.isSuccess) {
                    onDismissDialog()
                    loadTires() // Refresh list
                } else {
                    _uiState.update { it.copy(errorMessage = result.exceptionOrNull()?.message) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message) }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
                _uiState.update { it.copy(isSending = false) }
            }
        }
    }

    private fun loadTires() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val result = tireListUsecase()
                if (result.isSuccess) {
                    val tires = result.getOrNull() ?: emptyList()
                    _uiState.update { it.copy(tires = tires, displayedTires = tires) }
                } else {
                    _uiState.update { it.copy(errorMessage = result.exceptionOrNull()?.message) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message) }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun loadComboData() {
        viewModelScope.launch {
            val bearerToken = "Bearer ${token ?: ""}"
            try {
                val acqTypes = acquisitionTypeUseCase(bearerToken).getOrNull()
                val products = productListUseCase(bearerToken).getOrNull() ?: emptyList()

                _uiState.update {
                    it.copy(
                        acquisitionTypes = acqTypes ?: emptyList(),
                        products = products
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Error cargando datos de combos: ${e.message}") }
            }
        }
    }

    private fun loadTireDetails(tireId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingDialogData = true) }
            try {
                val result = tireGetUseCase(tireId)
                if (result.isSuccess) {
                    result.getOrNull()?.firstOrNull()?.let { details ->
                        _uiState.update {
                            it.copy(
                                dialogState = TireDialogState(
                                    id = details.idTire,
                                    selectedAcquisitionType = it.acquisitionTypes.find { type -> type.idAcquisitionType == details.typeAcquisitionId },
                                    selectedProduct = it.products.find { p -> p.idProduct == details.productId },
                                    acquisitionDate = details.acquisitionDate,
                                    cost = details.unitCost.toString(),
                                    treadDepth = details.treadDepth.toString(),
                                    tireNumber = details.tireNumber,
                                    dot = details.dot,
                                    folioFactura = details.document
                                )
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Error cargando detalles: ${e.message}") }
            } finally {
                _uiState.update { it.copy(isLoadingDialogData = false) }
            }
        }
    }

    private fun applyFilter() {
        val state = _uiState.value
        val filtered = if (state.searchQuery.isBlank()) {
            state.tires
        } else {
            state.tires.filter { tire ->
                tire.typeAcquisition.contains(state.searchQuery, ignoreCase = true) ||
                        tire.brand.contains(state.searchQuery, ignoreCase = true)
            }
        }
        _uiState.update { it.copy(displayedTires = filtered) }
    }

    private fun isDialogStateInvalid(state: TireDialogState): Boolean {
        return state.selectedAcquisitionType == null || state.selectedProduct == null ||
                state.acquisitionDate.isBlank() || state.cost.isBlank() || state.tireNumber.isBlank()
    }

    private fun formatAcquisitionDate(date: String): String {
        return try {
            val dateOnly = LocalDate.parse(date.substringBefore('T'), DateTimeFormatter.ISO_DATE)
            dateOnly.atStartOfDay().toString()
        } catch (_: Exception) {
            LocalDateTime.now().toString()
        }
    }
}
