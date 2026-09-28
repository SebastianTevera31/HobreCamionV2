package com.rfz.appflotal.presentation.ui.registertires

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfz.appflotal.R
import com.rfz.appflotal.core.util.Commons.getCurrentDate
import com.rfz.appflotal.data.model.CatalogItem
import com.rfz.appflotal.data.model.assembly.AssemblyTire
import com.rfz.appflotal.data.model.tire.dto.TireCrudDto
import com.rfz.appflotal.data.repository.UnidadOdometro
import com.rfz.appflotal.data.repository.database.HombreCamionRepository
import com.rfz.appflotal.domain.acquisitiontype.AcquisitionTypeUseCase
import com.rfz.appflotal.domain.assembly.AddAssemblyTireUseCase
import com.rfz.appflotal.domain.axle.GetAxlesUseCase
import com.rfz.appflotal.domain.database.GetTasksUseCase
import com.rfz.appflotal.domain.product.ProductListUseCase
import com.rfz.appflotal.domain.tire.TireCrudUseCase
import com.rfz.appflotal.domain.userpreferences.ObserveOdometerUnitUseCase
import com.rfz.appflotal.presentation.ui.assembly.viewmodel.OdometerValidation
import com.rfz.appflotal.presentation.ui.utils.OperationStatus
import com.rfz.appflotal.presentation.ui.utils.kmToMiles
import com.rfz.appflotal.presentation.ui.utils.milesToKm
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class RegisterTireViewModel @Inject constructor(
    private val tireCrudUseCase: TireCrudUseCase,
    private val acquisitionTypeUseCase: AcquisitionTypeUseCase,
    private val productListUseCase: ProductListUseCase,
    private val getAxleUseCase: GetAxlesUseCase,
    private val addAssemblyTireUseCase: AddAssemblyTireUseCase,
    private val getTasksUseCase: GetTasksUseCase,
    private val hombreCamionRepository: HombreCamionRepository,
    observeOdometerUnitUseCase: ObserveOdometerUnitUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterTireUiState())
    val uiState = _uiState.asStateFlow()

    private val odometerUnit = observeOdometerUnitUseCase().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        UnidadOdometro.KILOMETROS
    )

    fun loadData(positionTire: String) {
        _uiState.update {
            it.copy(
                positionTire = positionTire,
                acquisitionDate = LocalDateTime.now().toString(),
                screenLoadStatus = OperationStatus.Loading
            )
        }

        viewModelScope.launch {
            val userData = getTasksUseCase().first { it.isNotEmpty() }[0]
            val bearerToken = "Bearer ${userData.fld_token}"

            val acquisitionDeferred = async { acquisitionTypeUseCase(bearerToken) }
            val productsDeferred = async { productListUseCase(bearerToken) }
            val axleDeferred = async { getAxleUseCase() }
            val odometerDeferred = async { hombreCamionRepository.getOdometer() }

            val axleResult = axleDeferred.await()
            val odometer = odometerDeferred.await()

            if (axleResult.isSuccess) {
                val unit = odometerUnit.value
                val odometerValue =
                    if (unit == UnidadOdometro.KILOMETROS) odometer.odometer
                    else kmToMiles(odometer.odometer.toDouble())

                _uiState.update { currentUiState ->
                    currentUiState.copy(
                        acquisitionTypes = acquisitionDeferred.await().getOrNull() ?: emptyList(),
                        products = productsDeferred.await().getOrNull() ?: emptyList(),
                        axleList = axleResult.getOrNull() ?: emptyList(),
                        currentOdometer = odometerValue.toInt().toString(),
                        screenLoadStatus = OperationStatus.Success
                    )
                }
            } else {
                _uiState.update { it.copy(screenLoadStatus = OperationStatus.Error) }
            }
        }
    }

    fun updateAcquisitionType(item: CatalogItem?) {
        val match = _uiState.value.acquisitionTypes.find { it.idAcquisitionType == item?.id }
        _uiState.update { it.copy(selectedAcquisitionType = match) }
    }

    fun updateProduct(item: CatalogItem?) {
        val match = _uiState.value.products.find { it.idProduct == item?.id }
        _uiState.update {
            it.copy(
                selectedProduct = match,
                treadDepth = match?.treadDepth?.toString() ?: ""
            )
        }
    }

    fun updateAxle(item: CatalogItem?) {
        val match = _uiState.value.axleList.find { it.id == item?.id }
        _uiState.update { it.copy(selectedAxle = match) }
    }

    fun updateAcquisitionDate(date: String) = _uiState.update { it.copy(acquisitionDate = date) }

    fun updateCost(value: String) =
        _uiState.update { it.copy(cost = value.filter { c -> c.isDigit() || c == '.' }) }

    fun updateFolioFactura(value: String) = _uiState.update { it.copy(folioFactura = value) }

    fun updateTireNumber(value: String) = _uiState.update { it.copy(tireNumber = value) }

    fun updateDot(value: String) = _uiState.update { it.copy(dot = value) }

    fun updateOdometer(value: String) {
        val validation = if (value.isNotEmpty()) {
            val currentOdometer = _uiState.value.currentOdometer.toIntOrNull() ?: 0
            if (value.toIntOrNull() == null || value.toInt() < currentOdometer) {
                OdometerValidation.INVALID
            } else {
                OdometerValidation.VALID
            }
        } else {
            OdometerValidation.EMPTY
        }
        _uiState.update { it.copy(odometer = value, isOdometerValid = validation) }
    }

    fun clearError() = _uiState.update { it.copy(errorMessage = null) }

    fun restartOperationStatus() = _uiState.update { it.copy(operationStatus = null) }

    fun saveAndMount(context: Context) {
        val state = _uiState.value

        if (state.selectedAcquisitionType == null || state.selectedProduct == null ||
            state.selectedAxle == null || state.acquisitionDate.isBlank() ||
            state.cost.isBlank() || state.tireNumber.isBlank() || state.dot.isBlank()
        ) {
            _uiState.update { it.copy(errorMessage = context.getString(R.string.Campos_requeridos_por_completar)) }
            return
        }

        val costValue = state.cost.toDoubleOrNull()
        val treadDepthValue = state.treadDepth.toIntOrNull()
        if (costValue == null || costValue <= 0 || treadDepthValue == null || treadDepthValue <= 0) {
            _uiState.update { it.copy(errorMessage = context.getString(R.string.error_solo_numeros_profunidad_costo)) }
            return
        }

        if (state.dot.trim().length > 10) {
            _uiState.update { it.copy(errorMessage = context.getString(R.string.dot_excedio_caracteres)) }
            return
        }

        if (state.tireNumber.trim().length > 15) {
            _uiState.update { it.copy(errorMessage = context.getString(R.string.numero_llanta_excedio_caracteres)) }
            return
        }

        if (state.isOdometerValid != OdometerValidation.VALID) {
            _uiState.update { it.copy(errorMessage = context.getString(R.string.error_odometro_inferior)) }
            return
        }

        _uiState.update { it.copy(operationStatus = OperationStatus.Loading) }

        viewModelScope.launch {
            val userData = getTasksUseCase().first { it.isNotEmpty() }[0]
            val bearerToken = "Bearer ${userData.fld_token}"

            val tireDto = TireCrudDto(
                idTire = 0,
                typeAcquisitionId = state.selectedAcquisitionType.idAcquisitionType,
                providerId = 0,
                productId = state.selectedProduct.idProduct,
                acquisitionDate = formatAcquisitionDate(state.acquisitionDate),
                document = state.folioFactura,
                unitCost = costValue.toInt(),
                dot = state.dot,
                tireNumber = state.tireNumber,
                treadDepth = treadDepthValue,
                registrationDate = LocalDateTime.now().toString(),
                isActive = true,
                retreadDesignId = 0,
                destination = 2,
                lifecycle = 0
            )

            val tireResult = tireCrudUseCase(bearerToken, tireDto)
            val newTireId = tireResult.getOrNull()?.id

            if (tireResult.isFailure || newTireId == null) {
                _uiState.update {
                    it.copy(
                        operationStatus = OperationStatus.Error,
                        errorMessage = tireResult.exceptionOrNull()?.message
                    )
                }
                return@launch
            }

            val odometerNumber = state.odometer.toDouble()
            val odometerValue =
                if (odometerUnit.value == UnidadOdometro.KILOMETROS) odometerNumber
                else milesToKm(odometerNumber)

            val assemblyResult = addAssemblyTireUseCase(
                AssemblyTire(
                    idAxle = state.selectedAxle.id,
                    idTire = newTireId,
                    positionTire = state.positionTire,
                    odometer = odometerValue.roundToInt(),
                    assemblyDate = getCurrentDate(),
                    updatedAt = System.currentTimeMillis()
                )
            )

            launch {
                hombreCamionRepository.updateOdometer(odometerValue.roundToInt(), getCurrentDate())
            }

            _uiState.update {
                it.copy(
                    operationStatus = if (assemblyResult.isSuccess) OperationStatus.Success else OperationStatus.Error
                )
            }
        }
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
