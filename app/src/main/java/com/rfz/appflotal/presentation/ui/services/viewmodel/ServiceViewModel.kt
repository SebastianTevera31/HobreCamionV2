package com.rfz.appflotal.presentation.ui.services.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rfz.appflotal.data.model.services.dto.ServiceDetailDto
import com.rfz.appflotal.data.repository.services.ServicesPagingSource
import com.rfz.appflotal.domain.database.GetTasksUseCase
import com.rfz.appflotal.domain.odometer.UpdateOdometerUseCase
import com.rfz.appflotal.domain.service.DeleteServiceUseCase
import com.rfz.appflotal.domain.service.DoCrudServiceDetailUseCase
import com.rfz.appflotal.domain.service.GetServicesUseCase
import com.rfz.appflotal.domain.service.GetTypeServiceUseCase
import com.rfz.appflotal.presentation.ui.services.ServiceFormData
import com.rfz.appflotal.presentation.ui.services.model.CatalogItemUi
import com.rfz.appflotal.presentation.ui.services.model.ServiceUi
import com.rfz.appflotal.presentation.ui.services.model.VehicleHeaderUi
import com.rfz.appflotal.presentation.ui.updateuserscreen.viewmodel.toVehicleData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
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
    val lastOdometer: Int = 0,          // último odómetro del vehículo (valor inicial del formulario)
    val services: List<ServiceUi> = emptyList(),
    val serviceTypes: List<CatalogItemUi> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isOffline: Boolean = false,
    val deleteFailed: Boolean = false,
    val isSaving: Boolean = false,      // guardado en curso (formulario bloqueado)
    val saveSucceeded: Boolean = false, // evento: el backend confirmó el guardado
    val saveFailed: Boolean = false,    // evento: el guardado falló
    val query: String = "",
    val page: Int = 0,           // página pedida (base 0); usar [currentPage]
) {
    /** Servicios que coinciden con la búsqueda. */
    val filteredServices: List<ServiceUi>
        get() {
            val q = query.trim()
            return if (q.isEmpty()) services else services.filter {
                it.description.contains(q, ignoreCase = true) ||
                        it.type.contains(q, ignoreCase = true)
            }
        }

    /** Total de páginas (mínimo 1, aunque no haya servicios). */
    val pageCount: Int
        get() = ((filteredServices.size + SERVICES_PAGE_SIZE - 1) / SERVICES_PAGE_SIZE)
            .coerceAtLeast(1)

    /** Página actual dentro de rango (p. ej. si al eliminar desaparece la última). */
    val currentPage: Int get() = page.coerceIn(0, pageCount - 1)
}

/** Servicios por página en la lista. */
private const val SERVICES_PAGE_SIZE = 10

@HiltViewModel
class ServiceViewModel @Inject constructor(
    private val getTasksUseCase: GetTasksUseCase,
    private val getServicesUseCase: GetServicesUseCase,
    private val getTypeServiceUseCase: GetTypeServiceUseCase,
    private val doCrudServiceDetailUseCase: DoCrudServiceDetailUseCase,
    private val deleteServiceUseCase: DeleteServiceUseCase,
    private val updateOdometerUseCase: UpdateOdometerUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ServiceUiState())
    val uiState = _uiState.asStateFlow()

    init {
        observeOdometer()
    }

    /**
     * Observa el odómetro vigente en Room: cambia al dar de alta un servicio (o desde otros
     * módulos y el worker de mantenimiento) y el formulario siempre inicia con él.
     */
    private fun observeOdometer() {
        viewModelScope.launch {
            runCatching {
                getTasksUseCase().collect { users ->
                    val odometer = users.firstOrNull()?.odometer ?: return@collect
                    _uiState.update { state ->
                        state.copy(
                            lastOdometer = odometer,
                            vehicle = state.vehicle?.copy(odometer = odometer.toString())
                        )
                    }
                }
            }
        }
    }

    // Ids con una eliminación en curso, para ignorar toques repetidos.
    private val deletingIds = mutableSetOf<Int>()

    // Vehículo dueño de los servicios; necesario para el CRUD (p_vehicle_fk_1).
    private var vehicleId: Int = 0

    // Carga en curso (o la última); null = aún no se ha cargado nunca.
    private var loadJob: Job? = null

    /**
     * Página actual de la lista (Paging 3). Se reconstruye al cambiar los servicios,
     * la búsqueda o la página; cachedIn la conserva al rotar o al volver del detalle.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val pagedServices: Flow<PagingData<ServiceUi>> = _uiState
        .map { it.filteredServices to it.currentPage }
        .distinctUntilChanged()
        .flatMapLatest { (services, page) ->
            Pager(
                config = PagingConfig(
                    pageSize = SERVICES_PAGE_SIZE,
                    initialLoadSize = SERVICES_PAGE_SIZE,
                    enablePlaceholders = false
                ),
                pagingSourceFactory = {
                    ServicesPagingSource(services, page = page, pageSize = SERVICES_PAGE_SIZE)
                }
            ).flow
        }
        .cachedIn(viewModelScope)

    fun serviceById(id: Int): ServiceUi? =
        _uiState.value.services.firstOrNull { it.id == id }

    /** Buscar vuelve a la primera página. */
    fun onQueryChange(query: String) {
        _uiState.update { it.copy(query = query, page = 0) }
    }

    /** [page] en base 1, como lo entrega PageNavigator. */
    fun goToPage(page: Int) {
        _uiState.update { it.copy(page = (page - 1).coerceIn(0, it.pageCount - 1)) }
    }

    /**
     * Carga inicial del módulo: solo la primera vez que se muestra la lista. Las
     * recargas posteriores (tras guardar, reintentar) pasan por [load].
     */
    fun loadIfNeeded() {
        if (loadJob == null) load()
    }

    /**
     * (Re)carga vehículo, servicios y tipos. Si ya hay una carga en curso se cancela y
     * se reinicia, para que la respuesta siempre refleje el estado más reciente del
     * backend (p. ej. una carga lanzada antes de que terminara un guardado).
     */
    fun load() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
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
                        lastOdometer = driverData.odometer,
                        services = services?.sortedByDescending { service -> service.id }
                            ?: emptyList(),
                        serviceTypes = types ?: emptyList(),
                        isLoading = false
                    )
                }
            } catch (e: CancellationException) {
                throw e // la reemplazó una carga más nueva; no tocar el estado
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun retry() = load()

    /**
     * Alta (serviceId == null) o edición (serviceId != null) de un servicio.
     *
     * El formulario se queda abierto mientras [ServiceUiState.isSaving]; al terminar
     * emite [ServiceUiState.saveSucceeded] (el formulario regresa a la lista, que ya
     * se está recargando) o [ServiceUiState.saveFailed] (el usuario sigue en el
     * formulario con sus datos para reintentar).
     */
    fun saveService(serviceId: Int?, data: ServiceFormData) {
        if (_uiState.value.isSaving) return
        _uiState.update { it.copy(isSaving = true, saveSucceeded = false, saveFailed = false) }
        viewModelScope.launch {
            if (vehicleId == 0) {
                runCatching {
                    val driverData = getTasksUseCase().first().firstOrNull()
                    if (driverData != null) {
                        vehicleId = driverData.idVehicle
                    }
                }
            }
            val dto = ServiceDetailDto(
                idService = serviceId ?: 0,
                description = data.description,
                idServiceType = data.typeId ?: 0,
                price = data.price.toDoubleOrNull() ?: 0.0,
                date = data.date,
                provider = data.provider,
                quantity = data.quantity.toIntOrNull() ?: 0,
                vehicleId = vehicleId,
                odometer = data.odometer.toIntOrNull() ?: 0,
                notas = data.notas,
                image = data.image,
            )
            // null = el backend no confirmó el guardado (error de red/HTTP).
            if (doCrudServiceDetailUseCase(dto) != null) {
                // Solo el alta captura odómetro: al editar es de solo lectura (trazabilidad).
                if (serviceId == null) runCatching { updateOdometerUseCase(dto.odometer) }
                _uiState.update { it.copy(isSaving = false, saveSucceeded = true) }
                load()
            } else {
                _uiState.update { it.copy(isSaving = false, saveFailed = true) }
            }
        }
    }

    fun consumeSaveSucceeded() {
        _uiState.update { it.copy(saveSucceeded = false) }
    }

    fun clearSaveError() {
        _uiState.update { it.copy(saveFailed = false) }
    }

    /**
     * Elimina el servicio en el backend y, solo si tuvo éxito, lo quita de la lista.
     * Si falla, el servicio se conserva y se avisa con [ServiceUiState.deleteFailed].
     */
    fun deleteService(serviceId: Int) {
        if (!deletingIds.add(serviceId)) return
        viewModelScope.launch {
            deleteServiceUseCase(serviceId)
                .onSuccess {
                    _uiState.update { state ->
                        state.copy(services = state.services.filterNot { it.id == serviceId })
                    }
                }
                .onFailure {
                    _uiState.update { it.copy(deleteFailed = true) }
                }
            deletingIds.remove(serviceId)
        }
    }

    fun clearDeleteError() {
        _uiState.update { it.copy(deleteFailed = false) }
    }
}
