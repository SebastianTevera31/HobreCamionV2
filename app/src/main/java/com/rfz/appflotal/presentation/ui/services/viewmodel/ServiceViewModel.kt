package com.rfz.appflotal.presentation.ui.services.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfz.appflotal.domain.database.GetTasksUseCase
import com.rfz.appflotal.presentation.ui.services.NewOrderData
import com.rfz.appflotal.presentation.ui.services.ServiceFormData
import com.rfz.appflotal.presentation.ui.services.model.CatalogItemUi
import com.rfz.appflotal.presentation.ui.services.model.ServiceItemUi
import com.rfz.appflotal.presentation.ui.services.model.ServiceOrderUi
import com.rfz.appflotal.presentation.ui.services.model.VehicleHeaderUi
import com.rfz.appflotal.presentation.ui.services.model.sampleOccurrenceTypes
import com.rfz.appflotal.presentation.ui.services.model.sampleProviders
import com.rfz.appflotal.presentation.ui.services.model.sampleServiceCatalog
import com.rfz.appflotal.presentation.ui.services.model.sampleServiceItems
import com.rfz.appflotal.presentation.ui.services.model.sampleServiceOrders
import com.rfz.appflotal.presentation.ui.updateuserscreen.viewmodel.toVehicleData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel del módulo de servicios.
 *
 * Se comparte entre todas las pantallas del grafo (lista, detalle, formularios)
 * porque se obtiene con hiltViewModel(parentEntry) sobre el ServiceGraph. Así los
 * datos cargados (órdenes, detalles, catálogos) viven una sola vez y no se pasan
 * por argumentos de navegación ni se recargan al cambiar de pantalla.
 *
 * De momento sirve datos de muestra para poder navegar el flujo completo. Cuando
 * existan los endpoints (GetServiceOrders / GetServiceOrderDetails) se sustituyen
 * los datos iniciales por llamadas a los casos de uso, igual que en AlertViewModel.
 */
data class ServiceUiState(
    val vehicle: VehicleHeaderUi? = null,
    val orders: List<ServiceOrderUi> = sampleServiceOrders,
    val items: List<ServiceItemUi> = sampleServiceItems,
    val serviceCatalog: List<CatalogItemUi> = sampleServiceCatalog,
    val providers: List<CatalogItemUi> = sampleProviders,
    val occurrenceTypes: List<CatalogItemUi> = sampleOccurrenceTypes,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isOffline: Boolean = false,
)

@HiltViewModel
class ServiceViewModel @Inject constructor(
    private val getTasksUseCase: GetTasksUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ServiceUiState())
    val uiState = _uiState.asStateFlow()

    fun orderById(id: Int): ServiceOrderUi? =
        _uiState.value.orders.firstOrNull { it.id == id }

    /** Servicios de una orden. Mock: devuelve todos; con backend filtrar por orderId. */
    fun itemsOfOrder(orderId: Int): List<ServiceItemUi> = _uiState.value.items

    fun itemById(id: Int): ServiceItemUi? =
        _uiState.value.items.firstOrNull { it.id == id }

    /** El vehículo dueño de las órdenes, como opción única del selector. */
    fun vehiclesForPicker(): List<CatalogItemUi> =
        listOf(CatalogItemUi(id = 0, name = _uiState.value.vehicle?.description ?: ""))

    // --- Acciones (pendientes de conectar al backend) --------------------------

    fun init() {
        viewModelScope.launch {
            val driverData = getTasksUseCase().first()[0]
            val vehicleData = driverData.toVehicleData()
            _uiState.update { currentUiState ->
                currentUiState.copy(
                    vehicle = VehicleHeaderUi(
                        economicNumber = vehicleData.plates,
                        description = vehicleData.typeVehicle,
                        odometer = driverData.odometer.toString()
                    )
                )
            }
        }
    }

    fun retry() {
        // TODO: recargar órdenes desde el caso de uso.
    }

    fun deleteOrder(orderId: Int) {
        // TODO: doCrudServiceOrder en modo eliminar. Por ahora, actualiza el estado local.
        _uiState.value = _uiState.value.copy(
            orders = _uiState.value.orders.filterNot { it.id == orderId }
        )
    }

    fun deleteService(serviceId: Int) {
        // TODO: doCrudServiceDetail en modo eliminar.
        _uiState.value = _uiState.value.copy(
            items = _uiState.value.items.filterNot { it.id == serviceId }
        )
    }

    fun saveService(orderId: Int, serviceId: Int?, data: ServiceFormData) {
        // TODO: mapear ServiceFormData -> ServiceDetailDto y llamar a doCrudServiceDetail.
    }

    fun saveOrder(orderId: Int?, data: NewOrderData) {
        // TODO: mapear NewOrderData -> ServiceOrderDto y llamar a doCrudServiceOrder.
    }
}
