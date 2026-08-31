package com.rfz.appflotal.presentation.ui.registrovehiculosscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfz.appflotal.data.model.base.BaseResponse
import com.rfz.appflotal.data.model.controltype.response.ControlTypeResponse
import com.rfz.appflotal.data.model.route.response.RouteResponse
import com.rfz.appflotal.data.model.vehicle.dto.VehicleCrudDto
import com.rfz.appflotal.data.model.vehicle.response.*
import com.rfz.appflotal.domain.base.BaseUseCase
import com.rfz.appflotal.domain.controltype.ControlTypeUseCase
import com.rfz.appflotal.domain.route.RouteUseCase
import com.rfz.appflotal.domain.vehicle.*
import com.rfz.appflotal.presentation.ui.utils.OperationStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class VehicleUiState(
    val vehicles: List<VehicleListResponse> = emptyList(),
    val filteredVehicles: List<VehicleListResponse> = emptyList(),
    val vehicleTypes: List<TypeVehicleResponse> = emptyList(),
    val controlTypes: List<ControlTypeResponse> = emptyList(),
    val routes: List<RouteResponse> = emptyList(),
    val bases: List<BaseResponse> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val searchQuery: String = "",
    val operationStatus: OperationStatus = OperationStatus.Idle
)

@HiltViewModel
class VehicleViewModel @Inject constructor(
    private val vehicleListUseCase: VehicleListUseCase,
    private val vehicleCrudUseCase: VehicleCrudUseCase,
    private val vehicleByIdUseCase: VehicleByIdUseCase,
    private val vehicleTypeUseCase: VehicleTypeUseCase,
    private val controlTypeUseCase: ControlTypeUseCase,
    private val routeUseCase: RouteUseCase,
    private val baseUseCase: BaseUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(VehicleUiState())
    val uiState = _uiState.asStateFlow()

    fun loadInitialData(token: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val bearerToken = "Bearer $token"
                val vehicleListResult = vehicleListUseCase(bearerToken)
                val typeResult = vehicleTypeUseCase(bearerToken)
                val controlResult = controlTypeUseCase(bearerToken)
                val routeResult = routeUseCase(bearerToken)
                val baseResult = baseUseCase(bearerToken)

                _uiState.update { current ->
                    current.copy(
                        vehicles = vehicleListResult.getOrNull() ?: emptyList(),
                        vehicleTypes = typeResult.getOrNull() ?: emptyList(),
                        controlTypes = controlResult.getOrNull() ?: emptyList(),
                        routes = routeResult.getOrNull() ?: emptyList(),
                        bases = baseResult.getOrNull() ?: emptyList(),
                        isLoading = false
                    )
                }
                applyFilter()
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        applyFilter()
    }

    private fun applyFilter() {
        val query = _uiState.value.searchQuery
        val all = _uiState.value.vehicles
        val filtered = if (query.isBlank()) {
            all
        } else {
            all.filter { 
                it.fldVehicleNumber.contains(query, ignoreCase = true) || 
                it.fldPlates.contains(query, ignoreCase = true) 
            }
        }
        _uiState.update { it.copy(filteredVehicles = filtered) }
    }

    fun saveVehicle(token: String, dto: VehicleCrudDto) {
        viewModelScope.launch {
            _uiState.update { it.copy(operationStatus = OperationStatus.Loading) }
            val result = vehicleCrudUseCase(dto, "Bearer $token")
            if (result.isSuccess) {
                _uiState.update { it.copy(operationStatus = OperationStatus.Success) }
            } else {
                _uiState.update { 
                    it.copy(
                        operationStatus = OperationStatus.Error,
                        errorMessage = result.exceptionOrNull()?.message ?: "Error al guardar"
                    ) 
                }
            }
        }
    }

    fun resetOperationStatus() {
        _uiState.update { it.copy(operationStatus = OperationStatus.Idle) }
    }
}
