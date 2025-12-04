package com.rfz.appflotal.presentation.ui.cambiodestino.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfz.appflotal.data.model.tire.ChangeDestination
import com.rfz.appflotal.data.model.tire.toTire
import com.rfz.appflotal.data.repository.tire.TireRepository
import com.rfz.appflotal.domain.destination.DestinationUseCase
import com.rfz.appflotal.domain.tire.TireListUsecase
import com.rfz.appflotal.presentation.ui.utils.OperationStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import java.time.ZoneOffset
import javax.inject.Inject

@HiltViewModel
class CambioDestinoViewModel @Inject constructor(
    private val tireUseCase: TireListUsecase,
    private val tireRepository: TireRepository,
    private val destinationUseCase: DestinationUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(CambioDestinoUiState())
    val uiState = _uiState.asStateFlow()

    fun loadData() {
        viewModelScope.launch {
            val tireDeferred = async { tireUseCase() }
            val destinationDeferred = async { destinationUseCase() }

            val tire = tireDeferred.await()
            val destination = destinationDeferred.await()

            if (tire.isSuccess && destination.isSuccess) {
                val destinationList = destination.getOrNull()?.filter {
                    it.id == 1 || it.id == 3 || it.id == 6
                } ?: emptyList()
                val tireList = tire.getOrNull()?.map { it.toTire() } ?: emptyList()

                _uiState.update { currentUiState ->
                    currentUiState.copy(
                        tires = tireList,
                        selectedTireList = tireList,
                        originList = destinationList,
                        destinationList = destinationList,
                        screenLoadStatus = OperationStatus.Success
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
    }

    fun onSelectedDestination(destinationId: Int?) {
        val uiState = _uiState.value
        uiState.destinationList.find { it.id == destinationId }?.let {
            _uiState.update { currentUiState ->
                currentUiState.copy(
                    form = currentUiState.form.copy(
                        selectedDestination = it
                    ),
                )
            }
        }
    }

    fun onSelectedOrigin(destinationId: Int?) {
        val uiState = _uiState.value
        uiState.originList.find { it.id == destinationId }?.let {
            val description = when (it.id) {
                1 -> "Reparar"
                3 -> "Desechar"
                6 -> "Renovar"
                else -> ""
            }

            val newDestinationList =
                uiState.originList.filter { destination -> destination.id != destinationId }

            val selectedTire = uiState.tires.filter { tire ->
                tire.destination == description
            }

            _uiState.update { currentUiState ->
                currentUiState.copy(
                    form = currentUiState.form.copy(
                        selectedOrigin = it
                    ),
                    destinationList = newDestinationList,
                    selectedTireList = selectedTire
                )
            }
        }
    }

    fun onSelectedTire(tireId: Int?) {
        _uiState.value.selectedTireList.find { it.id == tireId }?.let {
            _uiState.update { currentUiState ->
                currentUiState.copy(
                    form = currentUiState.form.copy(
                        selectedTire = it
                    )
                )
            }
        }
    }

    fun onReasonChange(reason: String) {
        _uiState.update { currentUiState ->
            currentUiState.copy(
                form = currentUiState.form.copy(
                    reason = reason
                )
            )
        }
    }

    fun onSendTireToDestination() {
        viewModelScope.launch {
            val nowUtc: OffsetDateTime = OffsetDateTime.now(ZoneOffset.UTC)
            val tireId = _uiState.value.form.selectedTire?.id
            val destinationId = _uiState.value.form.selectedDestination?.id
            val reason = _uiState.value.form.reason

            if (tireId == null || destinationId == null || reason.isBlank()) {
                _uiState.update { currentUiState ->
                    currentUiState.copy(
                        operationStatus = OperationStatus.Error
                    )
                }
                return@launch
            }

            val result = tireRepository.saveDestinationChange(
                changeDestination = ChangeDestination(
                    tireId = tireId,
                    destinationId = destinationId,
                    changeMotive = reason,
                    dateOperation = nowUtc
                )
            )

            if (result.isSuccess) {
                _uiState.update { currentUiState ->
                    currentUiState.copy(
                        operationStatus = OperationStatus.Success
                    )
                }
            } else {
                _uiState.update { currentUiState ->
                    currentUiState.copy(
                        operationStatus = OperationStatus.Error
                    )
                }
            }
        }
    }

    fun cleanUiState() {
        _uiState.value = CambioDestinoUiState()
    }

    fun cleanOperationStatus() {
        _uiState.update { currentUiState ->
            currentUiState.copy(
                operationStatus = null
            )
        }
    }
}