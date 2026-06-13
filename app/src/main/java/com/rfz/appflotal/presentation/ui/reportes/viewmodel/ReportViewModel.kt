package com.rfz.appflotal.presentation.ui.reportes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfz.appflotal.data.model.assembly.AssemblyTire
import com.rfz.appflotal.data.repository.assembly.AssemblyTireRepository
import com.rfz.appflotal.data.repository.report.ReportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReportUiState(
    val tireList: List<AssemblyTire> = emptyList(),
    val selectedTire: AssemblyTire? = null
)

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val reportRepository: ReportRepository,
    private val assemblyTireRepository: AssemblyTireRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportUiState())
    val uiState = _uiState.asStateFlow()

    fun loadData() {
        viewModelScope.launch {
            assemblyTireRepository.observeAssemblyTire().collect { tires ->
                _uiState.value = _uiState.value.copy(tireList = tires)
            }
        }
    }

    fun selectedTire(id: Int) {
        viewModelScope.launch {
            val tire = _uiState.value.tireList.find { it.idTire == id }
            _uiState.update { currentUiState ->
                currentUiState.copy(selectedTire = tire)
            }
        }
    }

    fun getCpkReport() {
        viewModelScope.launch {
            reportRepository.getCpkReport(0)
        }
    }

    fun getCO2EmissionsReport() {
        viewModelScope.launch {

        }
    }

    fun getFuelConsumptionReport() {
        viewModelScope.launch {

        }
    }
}