package com.rfz.appflotal.presentation.ui.fueling.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfz.appflotal.core.util.Commons
import com.rfz.appflotal.core.util.Commons.UTC_ISO_PATTERN
import com.rfz.appflotal.data.NetworkStatus
import com.rfz.appflotal.data.model.fueling.FuelingRecord
import com.rfz.appflotal.data.model.fueling.NewFueling
import com.rfz.appflotal.domain.database.GetTasksUseCase
import com.rfz.appflotal.domain.fueling.GetFuelTypesUseCase
import com.rfz.appflotal.domain.fueling.GetFuelingHistoryUseCase
import com.rfz.appflotal.domain.fueling.InsertFuelingUseCase
import com.rfz.appflotal.domain.wifi.WifiUseCase
import com.rfz.appflotal.presentation.ui.fueling.model.FuelingFormData
import com.rfz.appflotal.presentation.ui.fueling.model.FuelingSaveState
import com.rfz.appflotal.presentation.ui.fueling.model.FuelingUi
import com.rfz.appflotal.presentation.ui.services.model.CatalogItemUi
import com.rfz.appflotal.presentation.ui.services.model.VehicleHeaderUi
import com.rfz.appflotal.presentation.ui.updateuserscreen.viewmodel.toVehicleData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

private const val PAGE_SIZE = 10

/** Formato en que [DateFilterField] guarda las fechas del filtro. */
private val FILTER_DATE_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

data class FuelingUiState(
    val vehicle: VehicleHeaderUi? = null,
    val lastOdometer: Int = 0,
    val fuelTypes: List<CatalogItemUi> = emptyList(),
    val fuelTypesFailed: Boolean = false,
    val fuelings: List<FuelingUi> = emptyList(),
    val currentPage: Int = 0, // 0 = todavía no se cargó ninguna página
    val hasNextPage: Boolean = true,
    val total: Int = 0,
    // Filtros aplicados: fechas en dd/MM/yyyy ("" = sin límite); tipo null = todos.
    val startDate: String = "",
    val endDate: String = "",
    val fuelTypeId: Int? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isOffline: Boolean = false,
    val saveState: FuelingSaveState = FuelingSaveState.Idle,
)

/**
 * ViewModel del módulo de cargas de combustible.
 *
 * Se comparte entre historial y formulario vía hiltViewModel(parentEntry) sobre el
 * FuelingGraph, así el catálogo se pide una sola vez y el historial se refresca al guardar.
 */
@HiltViewModel
class FuelingViewModel @Inject constructor(
    private val getTasksUseCase: GetTasksUseCase,
    private val getFuelTypesUseCase: GetFuelTypesUseCase,
    private val getFuelingHistoryUseCase: GetFuelingHistoryUseCase,
    private val insertFuelingUseCase: InsertFuelingUseCase,
    private val wifiUseCase: WifiUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(FuelingUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadVehicle()
        loadFuelTypes()
        goToPage(1)
        viewModelScope.launch {
            wifiUseCase().collect { status ->
                _uiState.update { it.copy(isOffline = status != NetworkStatus.Connected) }
            }
        }
    }

    private fun loadVehicle() {
        viewModelScope.launch {
            val driverData = runCatching { getTasksUseCase().first().firstOrNull() }.getOrNull()
                ?: return@launch
            val vehicleData = driverData.toVehicleData()
            _uiState.update {
                it.copy(
                    vehicle = VehicleHeaderUi(
                        economicNumber = vehicleData.plates,
                        description = vehicleData.typeVehicle,
                        odometer = driverData.odometer.toString()
                    ),
                    lastOdometer = driverData.odometer
                )
            }
        }
    }

    fun loadFuelTypes() {
        viewModelScope.launch {
            _uiState.update { it.copy(fuelTypesFailed = false) }
            getFuelTypesUseCase()
                .onSuccess { types ->
                    _uiState.update { state ->
                        state.copy(fuelTypes = types.map { CatalogItemUi(it.id, it.description) })
                    }
                }
                .onFailure { _uiState.update { it.copy(fuelTypesFailed = true) } }
        }
    }

    fun goToPage(page: Int) {
        val state = _uiState.value
        if (state.isLoading || page < 1) return
        if (page > state.currentPage && state.currentPage > 0 && !state.hasNextPage) return

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            getFuelingHistoryUseCase(
                startDate = state.startDate.toStartOfDay(),
                endDate = state.endDate.toEndOfDay(),
                fuelTypeId = state.fuelTypeId,
                page = page,
                pageSize = PAGE_SIZE
            ).onSuccess { result ->
                _uiState.update {
                    it.copy(
                        fuelings = result.items.map { record -> record.toUi() },
                        currentPage = result.page,
                        hasNextPage = result.hasNextPage,
                        total = result.total,
                        isLoading = false
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.message ?: error.javaClass.simpleName
                    )
                }
            }
        }
    }

    fun retry() = goToPage(_uiState.value.currentPage.coerceAtLeast(1))

    fun applyFilter(startDate: String, endDate: String, fuelTypeId: Int?) {
        _uiState.update {
            it.copy(
                startDate = startDate,
                endDate = endDate,
                fuelTypeId = fuelTypeId,
                currentPage = 0,
                hasNextPage = true
            )
        }
        goToPage(1)
    }

    fun saveFueling(data: FuelingFormData) {
        if (_uiState.value.saveState == FuelingSaveState.Saving) return
        _uiState.update { it.copy(saveState = FuelingSaveState.Saving) }

        viewModelScope.launch {
            val fueling = NewFueling(
                fuelTypeId = data.fuelTypeId,
                fuelUnits = data.units.toDoubleOrNull() ?: 0.0,
                isLiter = data.isLiter,
                amount = data.amount.toDoubleOrNull() ?: 0.0,
                odometer = data.odometer.toIntOrNull() ?: 0,
                fuelingDate = data.dateMillis.toFuelingDate(),
                station = data.station
            )
            insertFuelingUseCase(fueling)
                .onSuccess {
                    _uiState.update { it.copy(saveState = FuelingSaveState.Saved) }
                    // La nueva carga es la más reciente: se vuelve a la primera página.
                    _uiState.update { it.copy(currentPage = 0, hasNextPage = true) }
                    goToPage(1)
                }
                .onFailure {
                    _uiState.update { it.copy(saveState = FuelingSaveState.Failed) }
                }
        }
    }

    /** La UI llama esto después de reaccionar a [FuelingSaveState.Saved] o [FuelingSaveState.Failed]. */
    fun consumeSaveState() {
        _uiState.update { it.copy(saveState = FuelingSaveState.Idle) }
    }
}

private fun FuelingRecord.toUi(): FuelingUi {
    val mapsUrl = if (hasCoordinates) {
        "https://www.google.com/maps/search/?api=1&query=%.6f,%.6f".format(Locale.US, lat, lon)
    } else null
    return FuelingUi(
        // El alta se envía en UTC; el historial llega sin zona y se interpreta como UTC.
        date = runCatching { Commons.formatToLongDate(fuelingDate, UTC_ISO_PATTERN) }.getOrDefault(
            fuelingDate
        ),
        fuelType = fuelType,
        units = units,
        amount = amount,
        station = station,
        odometer = "%,d".format(odometer),
        mapsUrl = mapsUrl
    )
}

/** dd/MM/yyyy -> inicio del día en ISO 8601; null si no hay filtro o no se puede leer. */
private fun String.toStartOfDay(): String? = toFilterDate()?.atStartOfDay()
    ?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

/** dd/MM/yyyy -> último segundo del día en ISO 8601; null si no hay filtro. */
private fun String.toEndOfDay(): String? = toFilterDate()?.atTime(LocalTime.of(23, 59, 59))
    ?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

private fun String.toFilterDate(): LocalDate? =
    takeIf { it.isNotBlank() }?.let {
        runCatching {
            LocalDate.parse(
                it,
                FILTER_DATE_FORMAT
            )
        }.getOrNull()
    }

/**
 * El DatePicker entrega la medianoche UTC del día elegido. Se conserva la hora actual del
 * dispositivo (para hoy es "ahora") y se envía en UTC, como espera insertFueling.
 */
private fun Long.toFuelingDate(): String {
    val day = Instant.ofEpochMilli(this).atZone(ZoneOffset.UTC).toLocalDate()
    val instant = day.atTime(LocalTime.now()).atZone(ZoneId.systemDefault()).toInstant()
    return Commons.getCurrentUtcDate(instant)
}
