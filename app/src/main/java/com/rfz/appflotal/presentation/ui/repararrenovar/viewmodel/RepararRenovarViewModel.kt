package com.rfz.appflotal.presentation.ui.repararrenovar.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfz.appflotal.data.model.repair.toDomain
import com.rfz.appflotal.data.model.tire.RepairedTire
import com.rfz.appflotal.data.model.tire.RetreatedTire
import com.rfz.appflotal.data.model.tire.toTire
import com.rfz.appflotal.data.repository.repair.RepairRepository
import com.rfz.appflotal.data.repository.tire.TireRepositoryImpl
import com.rfz.appflotal.domain.destination.DestinationUseCase
import com.rfz.appflotal.domain.retreaddesign.RetreadDesignListUseCase
import com.rfz.appflotal.domain.tire.TireGetUseCase
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
class RepararRenovarViewModel @Inject constructor(
    private val tireUseCase: TireListUsecase,
    private val tireGetUseCase: TireGetUseCase,
    private val destinationUseCase: DestinationUseCase,
    private val tireRepository: TireRepositoryImpl,
    private val retreadedDesignListUseCase: RetreadDesignListUseCase,
    private val repairRepository: RepairRepository
) : ViewModel() {
    private var _uiState = MutableStateFlow(RepararRenovarUiState())
    val uiState = _uiState.asStateFlow()

    fun loadData() {
        viewModelScope.launch {
            val destinationsDeferred = async { destinationUseCase() }
            val tiresDeferred = async { tireUseCase() }
            val retreadDesignDeferred = async { retreadedDesignListUseCase() }
            val repairCauseListDeferred = async {
                repairRepository.getRepairCatalog()
            }

            val destinations = destinationsDeferred.await()
            val tires = tiresDeferred.await()
            val retreadDesign = retreadDesignDeferred.await()
            val repairCauseList = repairCauseListDeferred.await()

            if (destinations.isSuccess && tires.isSuccess &&
                retreadDesign.isSuccess && repairCauseList.isSuccess
            ) {
                val repairRetreadDestinations =
                    destinations.getOrNull()?.filter { it.id == 1 || it.id == 6 } ?: emptyList()

                val repairedTires =
                    tires.getOrNull()?.filter { it.destination == "Reparar" }?.map { it.toTire() }
                        ?: emptyList()

                val retreadedTires =
                    tires.getOrNull()?.filter { it.destination == "Renovar" }?.map { it.toTire() }
                        ?: emptyList()

                val retreadDesignList = retreadDesign.getOrNull()?.map { it.toDomain() }
                    ?: emptyList()

                val repairCauseList =
                    repairCauseList.getOrNull()?.map { it.toDomain() } ?: emptyList()

                _uiState.update { currentUiState ->
                    currentUiState.copy(
                        destinationList = repairRetreadDestinations,
                        repairedTireList = repairedTires,
                        retreadedTireList = retreadedTires,
                        retreadDesignList = retreadDesignList,
                        repairCauseList = repairCauseList,
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

    fun updateSelectedTire(catalogItemId: Int, destinationId: Int) {
        viewModelScope.launch {
            val tireData = tireGetUseCase(catalogItemId)

            tireData.onSuccess { tireData ->
                if (destinationId == DestinationSelection.REPARAR.id) { // DestinationId == 1 -> Reparar
                    _uiState.value.repairedTireList.find { it.id == catalogItemId }?.let { tire ->
                        _uiState.update { currentUiState ->
                            currentUiState.copy(
                                selectedTire = tire,
                                tireCost = tireData.first().unitCost
                            )
                        }
                    }
                } else { // DestinationId == 6 -> Renovar
                    _uiState.value.retreadedTireList.find { it.id == catalogItemId }?.let { tire ->
                        _uiState.update { currentUiState ->
                            currentUiState.copy(
                                selectedTire = tire
                            )
                        }
                    }
                }
            }
        }
    }

    fun sendTire(destinationId: Int, tireId: Int, cost: Int, idSelection: Int) =
        viewModelScope.launch {
            val nowUtc: OffsetDateTime = OffsetDateTime.now(ZoneOffset.UTC)

            val result = if (destinationId == 1) {
                runCatching {
                    tireRepository.postRepairedTire(
                        repairedTire = RepairedTire(
                            id = 0,
                            tireId = tireId,
                            cost = cost.toDouble(),
                            repairId = idSelection,
                            dateOperation = nowUtc
                        )
                    )
                }
            } else {
                runCatching {
                    tireRepository.postRetreatedTire(
                        retreatedTire = RetreatedTire(
                            id = 0,
                            tireId = tireId,
                            cost = cost.toDouble(),
                            dateOperation = nowUtc,
                            retreadDesignId = idSelection
                        )
                    )
                }
            }


            if (result.isSuccess) {
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
            } else {
                _uiState.update { currentUiState ->
                    currentUiState.copy(
                        operationStatus = OperationStatus.Error
                    )
                }
            }
        }

    fun cleanOperationStatus() {
        _uiState.update { currentUiState ->
            currentUiState.copy(
                operationStatus = null
            )
        }
    }

    fun cleanUiState() {
        _uiState.value = RepararRenovarUiState()
    }
}