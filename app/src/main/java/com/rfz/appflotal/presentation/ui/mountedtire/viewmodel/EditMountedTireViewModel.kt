package com.rfz.appflotal.presentation.ui.mountedtire.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfz.appflotal.R
import com.rfz.appflotal.data.model.CatalogItem
import com.rfz.appflotal.data.model.tire.dto.TireCrudDto
import com.rfz.appflotal.data.model.tire.response.TirexIdResponse
import com.rfz.appflotal.data.repository.UnidadOdometro
import com.rfz.appflotal.domain.acquisitiontype.AcquisitionTypeUseCase
import com.rfz.appflotal.domain.assembly.GetAssemblyTireUseCase
import com.rfz.appflotal.domain.axle.GetAxlesUseCase
import com.rfz.appflotal.domain.database.GetTasksUseCase
import com.rfz.appflotal.domain.product.ProductListUseCase
import com.rfz.appflotal.domain.tire.TireCrudUseCase
import com.rfz.appflotal.domain.tire.TireGetUseCase
import com.rfz.appflotal.domain.userpreferences.ObserveOdometerUnitUseCase
import com.rfz.appflotal.presentation.ui.mountedtire.model.EditMountedTireUiState
import com.rfz.appflotal.presentation.ui.mountedtire.model.MountedTireSummary
import com.rfz.appflotal.presentation.ui.utils.OperationStatus
import com.rfz.appflotal.presentation.ui.utils.kmToMiles
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlin.math.roundToInt

/**
 * Edición de la ficha de una llanta ya montada. Toma el id de la llanta del montaje local
 * de la posición y la actualiza con api/Tire/CrudTire (id_tire != 0). No desmonta ni
 * modifica el montaje (eje, odómetro y fecha se muestran solo como referencia).
 */
@HiltViewModel
class EditMountedTireViewModel @Inject constructor(
    private val getAssemblyTireUseCase: GetAssemblyTireUseCase,
    private val tireGetUseCase: TireGetUseCase,
    private val tireCrudUseCase: TireCrudUseCase,
    private val acquisitionTypeUseCase: AcquisitionTypeUseCase,
    private val productListUseCase: ProductListUseCase,
    private val getAxlesUseCase: GetAxlesUseCase,
    private val getTasksUseCase: GetTasksUseCase,
    private val observeOdometerUnitUseCase: ObserveOdometerUnitUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditMountedTireUiState())
    val uiState = _uiState.asStateFlow()

    /** Ficha original: conserva los campos que esta pantalla no edita (proveedor, destino, vida…). */
    private var originalTire: TirexIdResponse? = null

    fun load(position: String) {
        val current = _uiState.value
        if (current.position == position && current.loadStatus == OperationStatus.Success) return

        _uiState.update {
            EditMountedTireUiState(position = position, loadStatus = OperationStatus.Loading)
        }

        viewModelScope.launch {
            val assembly = getAssemblyTireUseCase(position)
            if (assembly == null) {
                _uiState.update { it.copy(loadStatus = OperationStatus.Error) }
                return@launch
            }

            val bearerToken = "Bearer ${token()}"
            val tireDeferred = async { tireGetUseCase(assembly.idTire) }
            // Los catálogos se esperan antes de seleccionar valores para no dejarlos vacíos.
            val acquisitionDeferred = async { acquisitionTypeUseCase(bearerToken) }
            val productsDeferred = async { productListUseCase(bearerToken) }
            val axlesDeferred = async { getAxlesUseCase() }

            val tire = tireDeferred.await().getOrNull()?.firstOrNull()
            if (tire == null) {
                _uiState.update { it.copy(loadStatus = OperationStatus.Error) }
                return@launch
            }
            originalTire = tire

            val acquisitionTypes = acquisitionDeferred.await().getOrNull().orEmpty()
            val products = productsDeferred.await().getOrNull().orEmpty()
            val axle = axlesDeferred.await().getOrNull()?.find { it.id == assembly.idAxle }

            val unit = observeOdometerUnitUseCase().first()
            val odometer =
                if (unit == UnidadOdometro.KILOMETROS) assembly.odometer
                else kmToMiles(assembly.odometer.toDouble()).roundToInt()

            _uiState.update {
                it.copy(
                    loadStatus = OperationStatus.Success,
                    summary = MountedTireSummary(
                        axleDescription = axle?.description,
                        assemblyDate = assembly.assemblyDate.substringBefore('T'),
                        odometer = odometer,
                        odometerUnit = unit,
                    ),
                    acquisitionTypes = acquisitionTypes,
                    products = products,
                    selectedAcquisitionType = acquisitionTypes.find { type ->
                        type.idAcquisitionType == tire.typeAcquisitionId
                    },
                    selectedProduct = products.find { product -> product.idProduct == tire.productId },
                    acquisitionDate = tire.acquisitionDate,
                    folioFactura = tire.document,
                    cost = tire.unitCost.toString(),
                    treadDepth = tire.treadDepth.toString(),
                    tireNumber = tire.tireNumber,
                    dot = tire.dot,
                )
            }
        }
    }

    fun retry() {
        val position = _uiState.value.position
        _uiState.update { it.copy(loadStatus = OperationStatus.Idle) }
        load(position)
    }

    fun updateAcquisitionType(item: CatalogItem?) = _uiState.update { state ->
        state.copy(selectedAcquisitionType = state.acquisitionTypes.find { it.idAcquisitionType == item?.id })
    }

    fun updateProduct(item: CatalogItem?) = _uiState.update { state ->
        val product = state.products.find { it.idProduct == item?.id }
        state.copy(
            selectedProduct = product,
            treadDepth = product?.treadDepth?.toString() ?: state.treadDepth
        )
    }

    fun updateAcquisitionDate(date: String) = _uiState.update { it.copy(acquisitionDate = date) }

    fun updateFolioFactura(value: String) = _uiState.update { it.copy(folioFactura = value) }

    fun updateCost(value: String) =
        _uiState.update { it.copy(cost = value.filter { c -> c.isDigit() || c == '.' }) }

    fun updateTireNumber(value: String) = _uiState.update { it.copy(tireNumber = value) }

    fun updateDot(value: String) = _uiState.update { it.copy(dot = value) }

    fun clearError() = _uiState.update { it.copy(errorRes = null, errorMessage = null) }

    fun consumeSaveStatus() = _uiState.update { it.copy(saveStatus = OperationStatus.Idle) }

    fun save() {
        val state = _uiState.value
        val original = originalTire ?: return
        if (state.saveStatus == OperationStatus.Loading) return

        val acquisitionType = state.selectedAcquisitionType
        val product = state.selectedProduct
        if (!state.isFormValid || acquisitionType == null || product == null) {
            _uiState.update { it.copy(errorRes = R.string.Campos_requeridos_por_completar) }
            return
        }

        val cost = state.cost.trim().toDoubleOrNull()
        val treadDepth = state.treadDepth.trim().toIntOrNull()
        if (cost == null || cost <= 0 || treadDepth == null || treadDepth <= 0) {
            _uiState.update { it.copy(errorRes = R.string.error_solo_numeros_profunidad_costo) }
            return
        }

        _uiState.update { it.copy(saveStatus = OperationStatus.Loading) }

        viewModelScope.launch {
            val dto = TireCrudDto(
                idTire = original.idTire,
                typeAcquisitionId = acquisitionType.idAcquisitionType,
                providerId = original.providerId,
                acquisitionDate = formatAcquisitionDate(state.acquisitionDate),
                registrationDate = original.registrationDate,
                document = state.folioFactura.trim(),
                treadDepth = treadDepth,
                unitCost = cost.toInt(),
                tireNumber = state.tireNumber.trim(),
                productId = product.idProduct,
                dot = state.dot.trim(),
                isActive = original.isActive,
                retreadDesignId = original.retreadDesignId,
                destination = original.destinationId,
                lifecycle = original.lifecycle,
            )

            val result = tireCrudUseCase("Bearer ${token()}", dto)
            val response = result.getOrNull()
            Log.d(TAG, "CrudTire request=$dto response=$response error=${result.exceptionOrNull()?.message}")

            // El API responde HTTP 200 aun cuando no aplica el cambio; el resultado real viene
            // en el cuerpo (id = 200 si se guardó), igual que en el alta de RegisterTire.
            _uiState.update {
                if (response?.id == SUCCESS_ID) {
                    it.copy(saveStatus = OperationStatus.Success)
                } else {
                    it.copy(
                        saveStatus = OperationStatus.Error,
                        errorRes = R.string.error_editar_llanta,
                        errorMessage = response?.message ?: result.exceptionOrNull()?.message
                    )
                }
            }
        }
    }

    private companion object {
        const val TAG = "EditMountedTire"
        const val SUCCESS_ID = 200
    }

    private suspend fun token(): String =
        getTasksUseCase().first { it.isNotEmpty() }[0].fld_token

    private fun formatAcquisitionDate(date: String): String = try {
        LocalDate.parse(date.substringBefore('T'), DateTimeFormatter.ISO_DATE)
            .atStartOfDay()
            .toString()
    } catch (_: Exception) {
        LocalDateTime.now().toString()
    }
}
