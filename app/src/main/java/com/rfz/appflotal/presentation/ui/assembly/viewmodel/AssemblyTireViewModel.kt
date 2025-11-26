package com.rfz.appflotal.presentation.ui.assembly.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfz.appflotal.core.util.Commons.getCurrentDate
import com.rfz.appflotal.data.model.assembly.AssemblyTire
import com.rfz.appflotal.data.model.axle.toCatalogItem
import com.rfz.appflotal.data.model.tire.toCatalogItem
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
    private val getAxleUseCase: GetAxleDomain
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
            val tiresDeferred = async { tireUseCase() }
            val axleDeferred = async { getAxleUseCase() }
            val tiresResult = tiresDeferred.await()
            val axleResult = axleDeferred.await()

            if (tiresResult.isSuccess && axleResult.isSuccess) {
                val tiresList = tiresResult.getOrThrow()
                    .filter { it.destination == "Almacen" }
                    .map { it.toCatalogItem() }

                val axleList = axleResult.getOrThrow()
                    .map { it.toCatalogItem() }

                // Actualizamos el estado de la UI una sola vez
                _uiState.update { currentUiState ->
                    currentUiState.copy(
                        tireList = tiresList,
                        axleList = axleList,
                    )
                }
            } else {

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


    fun cleanUiState() {
        _uiState.update { currentUiState ->
            currentUiState.copy(
                operationStatus = null
            )
        }
    }
}