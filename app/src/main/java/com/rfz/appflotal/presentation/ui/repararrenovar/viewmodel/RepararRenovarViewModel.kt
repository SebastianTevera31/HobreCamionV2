package com.rfz.appflotal.presentation.ui.repararrenovar.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfz.appflotal.data.model.CatalogItem
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
            _uiState.update { it.copy(screenLoadStatus = OperationStatus.Loading) }
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

                val repairCauseListDomain =
                    repairCauseList.getOrNull()?.map { it.toDomain() } ?: emptyList()

                _uiState.update { currentUiState ->
                    currentUiState.copy(
                        originList = repairRetreadDestinations,
                        repairedTireList = repairedTires,
                        retreadedTireList = retreadedTires,
                        retreadDesignList = retreadDesignList,
                        repairCauseList = repairCauseListDomain,
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
            val tireDataResult = tireGetUseCase(catalogItemId)

            tireDataResult.onSuccess { tireData ->
                val tire = if (destinationId == DestinationSelection.REPARAR.id) {
                    _uiState.value.repairedTireList.find { it.id == catalogItemId }
                } else { // Renovar
                    _uiState.value.retreadedTireList.find { it.id == catalogItemId }
                }

                tire?.let { foundTire ->
                    val cost = tireData.firstOrNull()?.unitCost ?: 0
                    _uiState.update { currentState ->
                        currentState.copy(
                            selectedTire = foundTire,
                            tireCost = cost.toString()
                        )
                    }
                }
            }
        }
    }

    fun sendTire() =
        viewModelScope.launch {
            _uiState.update { it.copy(operationStatus = OperationStatus.Loading) }
            val nowUtc: OffsetDateTime = OffsetDateTime.now(ZoneOffset.UTC)
            val uiState = _uiState.value

            val tireId = uiState.selectedTire?.id
            val cost = uiState.tireCost.toDouble()
            val destinationId = uiState.selectedOrigin?.id
            val repairCauseId = uiState.selectedRepairCause?.id
            val retreadDesignId = uiState.selectedRetreadedDesign?.idDesign

            if (tireId == null || destinationId == null) {
                _uiState.update { it.copy(operationStatus = OperationStatus.Error) }
                return@launch
            }

            val result = if (destinationId == DestinationSelection.REPARAR.id) {
                if (repairCauseId == null) {
                    _uiState.update { it.copy(operationStatus = OperationStatus.Error) }
                    return@launch
                }
                runCatching {
                    tireRepository.postRepairedTire(
                        repairedTire = RepairedTire(
                            id = 0,
                            tireId = tireId,
                            cost = cost,
                            repairId = repairCauseId,
                            dateOperation = nowUtc
                        )
                    )
                }
            } else { // Renovar
                if (retreadDesignId == null) {
                    _uiState.update { it.copy(operationStatus = OperationStatus.Error) }
                    return@launch
                }
                runCatching {
                    tireRepository.postRetreatedTire(
                        retreatedTire = RetreatedTire(
                            id = 0,
                            tireId = tireId,
                            cost = cost,
                            dateOperation = nowUtc,
                            retreadDesignId = retreadDesignId
                        )
                    )
                }
            }

            _uiState.update { currentUiState ->
                currentUiState.copy(
                    operationStatus = if (result.isSuccess) OperationStatus.Success else OperationStatus.Error
                )
            }
        }

    fun updateRetreadedDesign(retreadBrandId: Int) {
        val retreadDesign =
            _uiState.value.retreadDesignList.find { it.idDesign == retreadBrandId }
        _uiState.update { currentUiState ->
            currentUiState.copy(
                selectedRetreadedDesign = retreadDesign
            )
        }
    }

    fun onSelectedDestination(destination: CatalogItem?) {
        _uiState.update {
            it.copy(
                selectedOrigin = destination,
                selectedTire = null,
                tireCost = "",
                selectedRepairCause = if (destination?.id != DestinationSelection.REPARAR.id) null else it.selectedRepairCause,
                selectedRetreadedDesign = if (destination?.id != DestinationSelection.RENOVAR.id) null else it.selectedRetreadedDesign
            )
        }
    }

    fun onSelectedRepairCause(cause: CatalogItem?) {
        _uiState.update { it.copy(selectedRepairCause = cause) }
    }

    fun updateCost(cost: String) {
        _uiState.update { currentUiState ->
            currentUiState.copy(
                tireCost = cost
            )
        }
    }

    fun deleteRetreadedDesign() {
        _uiState.update { currentUiState ->
            currentUiState.copy(
                selectedRetreadedDesign = null
            )
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