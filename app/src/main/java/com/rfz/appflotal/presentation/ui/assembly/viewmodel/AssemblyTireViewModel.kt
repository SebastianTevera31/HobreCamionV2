package com.rfz.appflotal.presentation.ui.assembly.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfz.appflotal.data.model.assembly.AssemblyTire
import com.rfz.appflotal.data.model.axle.Axle
import com.rfz.appflotal.data.model.tire.toTire
import com.rfz.appflotal.domain.assembly.AddAssemblyTire
import com.rfz.appflotal.domain.axle.GetAxleDomain
import com.rfz.appflotal.domain.tire.TireListUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
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

    fun loadDataList() {
        viewModelScope.launch {
            val listOfTires = async { tireUseCase() }
            val listOfAxle: Deferred<Result<List<Axle>>> = async { getAxleUseCase() }
            if (listOfTires.await().isSuccess && listOfAxle.await().isSuccess) {
                _uiState.update { currentUiState ->
                    currentUiState.copy(
                        tireList = listOfTires.await().getOrNull()?.map { it.toTire() },
                        axleList = listOfAxle.await().getOrNull(),
                    )
                }
            }
        }
    }

    fun registerAssemblyTire() {
        _uiState.update { currentUiState ->
            currentUiState.copy(
                operationStatus = OperationStatus.Loading
            )
        }

        viewModelScope.launch {
            val uiState = _uiState.value
            val result = addAssemblyTire(
                assemblyTire = AssemblyTire(
                    idAxle = uiState.axleSelected?.id ?: 0,
                    idTire = uiState.tireSelected?.id ?: 0,
                    positionTire = uiState.positionTire,
                    odometer = uiState.odometer,
                    assemblyDate = uiState.assemblyDate,
                    updatedAt = System.currentTimeMillis()
                )
            )
            _uiState.update { currentUiState ->
                currentUiState.copy(
                    operationStatus = result.fold(
                        onSuccess = { OperationStatus.AssemblyTire("OK") },
                        onFailure = { OperationStatus.Error("Error") }
                    )
                )
            }
        }
    }

    private fun validateAssemblyTireData() {

    }

    private fun cleanUiState() {

    }
}