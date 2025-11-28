package com.rfz.appflotal.presentation.ui.dissassembly.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfz.appflotal.core.util.AppLocale
import com.rfz.appflotal.core.util.Commons.getCurrentDate
import com.rfz.appflotal.data.model.CatalogItem
import com.rfz.appflotal.data.model.disassembly.tire.DisassemblyTire
import com.rfz.appflotal.data.model.tire.toTire
import com.rfz.appflotal.data.repository.assembly.AssemblyTireRepository
import com.rfz.appflotal.data.repository.disassembly.SetDisassemblyTireUseCase
import com.rfz.appflotal.domain.database.GetTasksUseCase
import com.rfz.appflotal.domain.destination.DestinationUseCase
import com.rfz.appflotal.domain.disassembly.DisassemblyCauseUseCase
import com.rfz.appflotal.domain.tire.TireListUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DisassemblyViewModel @Inject constructor(
    private val tireUseCase: TireListUsecase,
    private val getDestinationUseCase: DestinationUseCase,
    private val disassemblyCauseUseCase: DisassemblyCauseUseCase,
    private val setDisassemblyTireUseCase: SetDisassemblyTireUseCase,
    private val assemblyTireRepository: AssemblyTireRepository,
    private val getTasksUseCase: GetTasksUseCase,
) : ViewModel() {
    private var _uiState = MutableStateFlow(DisassemblyUiState())
    val uiState: StateFlow<DisassemblyUiState> = _uiState.asStateFlow()

    fun loadData(tirePosition: String) {
        _uiState.update { currentUiState ->
            currentUiState.copy(
                positionTire = tirePosition
            )
        }

        viewModelScope.launch {
            val tiresDeferred = async { tireUseCase() }
            val getDestinationDeferred = async { getDestinationUseCase() }
            val getMountedTireDeferred =
                async { assemblyTireRepository.getAssemblyTire(tirePosition) }
            val getDisassemblyCauseDeferred = async { disassemblyCauseUseCase() }

            val destinations = getDestinationDeferred.await()
            val tire = getMountedTireDeferred.await()
            val availableTireList = tiresDeferred.await()
            val disassemblyCauses = getDisassemblyCauseDeferred.await()

            if (destinations.isSuccess
                && tire != null
                && disassemblyCauses.isSuccess
                && availableTireList.isSuccess
            ) {
                val destinationsList =
                    destinations.getOrNull()?.filter { it.id == 1 || it.id == 3 || it.id == 6 }
                        ?: emptyList()

                val disassemblyList = disassemblyCauses.getOrNull() ?: emptyList()

                val tire = availableTireList.getOrNull()
                    ?.find { it.idTire == tire.idTire }
                    ?.toTire()

                _uiState.update { currentUiState ->
                    currentUiState.copy(
                        destinationList = destinationsList,
                        tire = tire,
                        disassemblyCauseList = disassemblyList,
                    )
                }
            }
        }
    }

    fun dismountTire(causeId: Int, destinationId: Int) {
        viewModelScope.launch {
            val odometer = getTasksUseCase().first()[0].odometer
            val result = setDisassemblyTireUseCase(
                DisassemblyTire(
                    disassemblyCause = causeId,
                    destination = destinationId,
                    dateOperation = getCurrentDate(),
                    positionTire = uiState.value.positionTire,
                    odometer = odometer
                )
            )

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

    fun restartOperationStatus() {
        _uiState.update { currentUiState ->
            currentUiState.copy(
                operationStatus = null
            )
        }
    }

    fun cleanUiState() {
        _uiState.value = DisassemblyUiState()
    }
}