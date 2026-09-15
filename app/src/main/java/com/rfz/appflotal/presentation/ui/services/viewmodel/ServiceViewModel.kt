package com.rfz.appflotal.presentation.ui.services.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfz.appflotal.data.model.services.dto.ServiceDetailDto
import com.rfz.appflotal.domain.database.GetTasksUseCase
import com.rfz.appflotal.domain.service.DoCrudServiceDetailUseCase
import com.rfz.appflotal.domain.service.GetServicesUseCase
import com.rfz.appflotal.domain.service.GetTypeServiceUseCase
import com.rfz.appflotal.presentation.ui.services.ServiceFormData
import com.rfz.appflotal.presentation.ui.services.model.CatalogItemUi
import com.rfz.appflotal.presentation.ui.services.model.ServiceUi
import com.rfz.appflotal.presentation.ui.services.model.VehicleHeaderUi
import com.rfz.appflotal.presentation.ui.updateuserscreen.viewmodel.toVehicleData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel del módulo de servicios (flujo "solo servicios", sin órdenes).
 *
 * Se comparte entre las pantallas del grafo (lista y formulario) vía
 * hiltViewModel(parentEntry) sobre el ServiceGraph, así los datos viven una sola
 * vez y la lista refleja los cambios tras guardar sin recargar de cero.
 */
data class ServiceUiState(
    val vehicle: VehicleHeaderUi? = null,
    val services: List<ServiceUi> = emptyList(),
    val serviceTypes: List<CatalogItemUi> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isOffline: Boolean = false,
)

@HiltViewModel
class ServiceViewModel @Inject constructor(
    private val getTasksUseCase: GetTasksUseCase,
    private val getServicesUseCase: GetServicesUseCase,
    private val getTypeServiceUseCase: GetTypeServiceUseCase,
    private val doCrudServiceDetailUseCase: DoCrudServiceDetailUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ServiceUiState())
    val uiState = _uiState.asStateFlow()

    // Vehículo dueño de los servicios; necesario para el CRUD (p_vehicle_fk_1).
    private var vehicleId: Int = 0

    fun serviceById(id: Int): ServiceUi? =
        _uiState.value.services.firstOrNull { it.id == id }

    fun load() {
        if (_uiState.value.isLoading) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val driverData = getTasksUseCase().first()[0]
                vehicleId = driverData.idVehicle
                val vehicleData = driverData.toVehicleData()

                val servicesDeferred = async { getServicesUseCase() }
                val typesDeferred = async { getTypeServiceUseCase() }
                val services = servicesDeferred.await()
                val types = typesDeferred.await()

                _uiState.update {
                    it.copy(
                        vehicle = VehicleHeaderUi(
                            economicNumber = vehicleData.plates,
                            description = vehicleData.typeVehicle,
                            odometer = driverData.odometer.toString()
                        ),
                        services = services ?: emptyList(),
                        serviceTypes = types ?: emptyList(),
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun retry() = load()

    /** Alta (serviceId == null) o edición (serviceId != null) de un servicio. */
    fun saveService(serviceId: Int?, data: ServiceFormData) {
        viewModelScope.launch {
            val dto = ServiceDetailDto(
                idService = serviceId ?: 0,
                description = data.description,
                idServiceType = data.typeId ?: 0,
                price = data.price.toIntOrNull() ?: 0,
                date = data.date,
                provider = data.provider,
                quantity = data.quantity.toIntOrNull() ?: 0,
                vehicleId = vehicleId
            )
            doCrudServiceDetailUseCase(dto)
            load()
        }
    }

    fun deleteService(serviceId: Int) {
        // TODO: eliminar en backend cuando exista el endpoint/acción.
        // Por ahora se refleja localmente para el flujo de UI.
        _uiState.update { state ->
            state.copy(services = state.services.filterNot { it.id == serviceId })
        }
    }
}
