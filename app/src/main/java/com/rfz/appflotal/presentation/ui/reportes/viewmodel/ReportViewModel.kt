package com.rfz.appflotal.presentation.ui.reportes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfz.appflotal.data.model.assembly.AssemblyTire
import com.rfz.appflotal.data.model.report.CpkReportResponse
import com.rfz.appflotal.data.model.tire.Tire
import com.rfz.appflotal.data.model.tire.toTire
import com.rfz.appflotal.data.repository.assembly.AssemblyTireRepository
import com.rfz.appflotal.data.repository.report.ReportRepository
import com.rfz.appflotal.domain.tire.TireListUsecase
import com.rfz.appflotal.presentation.ui.utils.LoadState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReportUiState(
    val tireList: List<AssemblyTire> = emptyList(),
    val detailTireList: List<Tire> = emptyList(),
    val selectedTire: AssemblyTire? = null,
    val tireInfo: Tire? = null,
    val menuLoadState: LoadState<Unit> = LoadState.Idle,
    val reportLoadState: LoadState<Unit> = LoadState.Idle,
    val performanceReport: List<Any> = emptyList(),
    val cpkReport: CpkReportResponse? = null
)

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val reportRepository: ReportRepository,
    private val assemblyTireRepository: AssemblyTireRepository,
    private val tireUseCase: TireListUsecase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportUiState())
    val uiState = _uiState.asStateFlow()

    fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(menuLoadState = LoadState.Loading) }

            val getTiresResult = tireUseCase().getOrNull()

            if (getTiresResult.isNullOrEmpty()) {
                _uiState.update { it.copy(menuLoadState = LoadState.Error("Error loading data")) }
                return@launch
            }

            assemblyTireRepository.refreshMountedTires().fold(
                onSuccess = {
                    assemblyTireRepository.observeAssemblyTire().collect { tires ->
                        val detailTires = getTiresResult.filter { it.destination == "Montada" }

                        _uiState.update { currentUiState ->
                            currentUiState.copy(
                                detailTireList = detailTires.map { it.toTire() },
                                tireList = tires,
                                menuLoadState = LoadState.Success(Unit)
                            )
                        }
                    }
                },
                onFailure = {
                    _uiState.update { it.copy(menuLoadState = LoadState.Error("Error loading data")) }
                }
            )
        }
    }

    fun getCpkReport(id: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(reportLoadState = LoadState.Loading) }

            val detailTires = _uiState.value.detailTireList.firstOrNull { it.id == id }
            val tire = _uiState.value.tireList.find { it.idTire == id }
            
            _uiState.update { it.copy(
                selectedTire = tire,
                tireInfo = detailTires
            ) }

            reportRepository.getCpkReport(id).onSuccess { reportList ->
                _uiState.update { currentUiState ->
                    currentUiState.copy(
                        cpkReport = reportList.firstOrNull(),
                        reportLoadState = LoadState.Success(Unit)
                    )
                }
            }.onFailure {
                _uiState.update { it.copy(reportLoadState = LoadState.Error("Error loading report")) }
            }
        }
    }

    fun resetReportState() {
        _uiState.update { it.copy(
            reportLoadState = LoadState.Idle,
            cpkReport = null,
            selectedTire = null,
            tireInfo = null
        ) }
    }

    fun getCO2EmissionsReport() {
        // Implement as needed
    }

    fun getFuelConsumptionReport() {
        // Implement as needed
    }
}