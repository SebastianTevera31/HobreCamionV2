package com.rfz.appflotal.presentation.ui.assembly.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfz.appflotal.core.util.Commons.getCurrentDate
import com.rfz.appflotal.data.model.assembly.AssemblyTire
import com.rfz.appflotal.data.model.tire.toTire
import com.rfz.appflotal.data.repository.database.HombreCamionRepository
import com.rfz.appflotal.domain.assembly.AddAssemblyTire
import com.rfz.appflotal.domain.axle.GetAxleDomain
import com.rfz.appflotal.domain.tire.TireListUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AssemblyTireViewModel @Inject constructor(
    private val tireUseCase: TireListUsecase,
    private val addAssemblyTire: AddAssemblyTire,
    private val getAxleUseCase: GetAxleDomain,
    private val hombreCamionRepository: HombreCamionRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(AssemblyTireUiState())
    val uiState = _uiState.asStateFlow()

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

            val tiresResult = tiresDeferred.await()
            val axleResult = axleDeferred.await()
            val odometer = odometerDeferred.await()

            if (tiresResult.isSuccess && axleResult.isSuccess) {
                val tiresList = tiresResult.getOrThrow()
                    .filter { it.destination == "Stock" }
                    .map { it.toTire() }

                val axleList = axleResult.getOrThrow()

                // Actualizamos el estado de la UI una sola vez
                _uiState.update { currentUiState ->
                    currentUiState.copy(
                        currentOdometer = odometer.toString(),
                        tireList = tiresList,
                        axleList = axleList,
                        isOdometerValid = OdometerValidation.VALID,
                        screenLoadStatus = ScreenLoadStatus.Success
                    )
                }
            } else {
                _uiState.update { currentUiState ->
                    currentUiState.copy(
                        screenLoadStatus = ScreenLoadStatus.Error
                    )
                }
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

        viewModelScope.launch {
            val uiState = _uiState.value
            val result = addAssemblyTire(
                assemblyTire = AssemblyTire(
                    idAxle = idAxle,
                    idTire = idTire,
                    positionTire = uiState.positionTire,
                    odometer = odometer.toInt(),
                    assemblyDate = getCurrentDate(),
                    updatedAt = System.currentTimeMillis()
                )
            )

            async { hombreCamionRepository.updateOdometer(odometer.toInt()) }

            _uiState.update { currentUiState ->
                currentUiState.copy(
                    operationStatus = result.fold(
                        onSuccess = {
                            OperationStatus.Success("OK")
                        },
                        onFailure = { OperationStatus.Error("Error") }
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