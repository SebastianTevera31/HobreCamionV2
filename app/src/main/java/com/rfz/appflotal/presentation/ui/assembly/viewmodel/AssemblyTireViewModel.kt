package com.rfz.appflotal.presentation.ui.assembly.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfz.appflotal.core.util.Commons.getCurrentDate
import com.rfz.appflotal.data.model.assembly.AssemblyTire
import com.rfz.appflotal.data.model.tire.toTire
import com.rfz.appflotal.data.repository.UnidadOdometro
import com.rfz.appflotal.data.repository.database.HombreCamionRepository
import com.rfz.appflotal.domain.assembly.AddAssemblyTireUseCase
import com.rfz.appflotal.domain.axle.GetAxlesUseCase
import com.rfz.appflotal.domain.tire.TireListUsecase
import com.rfz.appflotal.domain.userpreferences.ObserveOdometerUnitUseCase
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
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class AssemblyTireViewModel @Inject constructor(
    private val tireUseCase: TireListUsecase,
    private val addAssemblyTire: AddAssemblyTireUseCase,
    private val getAxleUseCase: GetAxlesUseCase,
    private val hombreCamionRepository: HombreCamionRepository,
    private val observeOdometerUnitUseCase: ObserveOdometerUnitUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AssemblyTireUiState())
    val uiState = _uiState.asStateFlow()

    private val odometerUnit = observeOdometerUnitUseCase().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        UnidadOdometro.KILOMETROS
    )

    fun loadDataList(positionTire: String) {
        _uiState.update { currentUiState ->
            currentUiState.copy(
                positionTire = positionTire
            )
        }

        viewModelScope.launch {
            val odometerDeferred = async { hombreCamionRepository.getOdometer() }
            val tiresDeferred = async { tireUseCase() }
            val axleDeferred = async { getAxleUseCase() }
            val odometerUnit = observeOdometerUnitUseCase().first()

            val tiresResult = tiresDeferred.await()
            val axleResult = axleDeferred.await()
            val odometer = odometerDeferred.await()

            if (tiresResult.isSuccess && axleResult.isSuccess) {
                val tiresList = tiresResult.getOrNull()
                    ?.filter { it.destination == "Stock" }
                    ?.map { it.toTire() } ?: emptyList()

                val axleList = axleResult.getOrNull() ?: emptyList()

                val odometerValue =
                    if (_uiState.value.odometerUnit == UnidadOdometro.KILOMETROS) odometer.odometer
                    else kmToMiles(odometer.odometer.toDouble())

                _uiState.update { currentUiState ->
                    currentUiState.copy(
                        currentOdometer = odometerValue.toInt().toString(),
                        tireList = tiresList,
                        axleList = axleList,
                        screenLoadStatus = OperationStatus.Success,
                        odometerUnit = odometerUnit
                    )
                }
            } else {
                _uiState.update { currentUiState ->
                    currentUiState.copy(
                        screenLoadStatus = OperationStatus.Error
                    )
                }
            }
        }

        observeOdometerChange()
    }

    fun observeOdometerChange() = viewModelScope.launch {
        odometerUnit.collect {
            _uiState.update { currentUiState ->
                currentUiState.copy(
                    odometerUnit = it
                )
            }
        }
    }

    fun registerAssemblyTire(
        odometer: String,
        idAxle: Int,
        idTire: Int,
    ) {
        _uiState.update { currentUiState ->
            currentUiState.copy(
                operationStatus = OperationStatus.Loading
            )
        }
        val odometerNumber = odometer.toDouble()
        val odometerValue =
            if (_uiState.value.odometerUnit == UnidadOdometro.KILOMETROS) odometerNumber
            else milesToKm(odometerNumber)


        viewModelScope.launch {
            val uiState = _uiState.value
            val result = addAssemblyTire(
                assemblyTire = AssemblyTire(
                    idAxle = idAxle,
                    idTire = idTire,
                    positionTire = uiState.positionTire,
                    odometer = odometerValue.roundToInt(),
                    assemblyDate = getCurrentDate(),
                    updatedAt = System.currentTimeMillis()
                )
            )

            async {
                hombreCamionRepository.updateOdometer(
                    odometerValue.roundToInt(),
                    getCurrentDate()
                )
            }

            _uiState.update { currentUiState ->
                currentUiState.copy(
                    operationStatus = result.fold(
                        onSuccess = {
                            OperationStatus.Success
                        },
                        onFailure = { OperationStatus.Error }
                    )
                )
            }
        }
    }

    fun updateTireField(tireId: Int?) {
        val tire = if (tireId != null) uiState.value.tireList.find { it.id == tireId } else null
        _uiState.update { currentUiState ->
            currentUiState.copy(
                currentTire = tire
            )
        }
    }

    fun validateOdometer(odometer: String) {
        val validation = if (odometer.isNotEmpty()) {
            if (odometer.toInt() < uiState.value.currentOdometer.toInt()) OdometerValidation.INVALID
            else OdometerValidation.VALID
        } else OdometerValidation.EMPTY
        _uiState.update { currentUiState ->
            currentUiState.copy(
                isOdometerValid = validation
            )
        }
    }

    fun restartOperationStatus() {
        _uiState.update { currentUiState ->
            currentUiState.copy(
                operationStatus = null
            )
        }
    }

    fun cleanUiState() {
        _uiState.value = AssemblyTireUiState()
    }
}