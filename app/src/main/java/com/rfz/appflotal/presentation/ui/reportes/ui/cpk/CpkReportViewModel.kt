package com.rfz.appflotal.presentation.ui.reportes.rendimiento.cpk

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfz.appflotal.data.model.assembly.AssemblyTire
import com.rfz.appflotal.data.model.report.CpkReportResponse
import com.rfz.appflotal.data.model.tire.Tire
import com.rfz.appflotal.domain.report.GetCpkReportUseCase
import com.rfz.appflotal.presentation.ui.reportes.pdf.sharePdf
import com.rfz.appflotal.presentation.ui.utils.LoadState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CpkReportViewModel @Inject constructor(
    private val getCpkReportUseCase: GetCpkReportUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CpkUiState())
    val uiState = _uiState.asStateFlow()

    private var allReports: List<CpkReportResponse> = emptyList()

    fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(menuLoadState = LoadState.Loading) }
            getCpkReportUseCase().fold(
                onSuccess = { data ->
                    allReports = data.allReports
                    _uiState.update { 
                        it.copy(
                            tireList = data.pairedTires,
                            detailTireList = data.detailedTires,
                            menuLoadState = LoadState.Success(Unit)
                        )
                    }
                },
                onFailure = {
                    _uiState.update { it.copy(menuLoadState = LoadState.Error(it.toString())) }
                }
            )
        }
    }

    fun selectTire(id: Int) {
        val detailTire = _uiState.value.detailTireList.find { it.id == id }
        val assemblyTire = _uiState.value.tireList.find { it.idTire == id }
        val report = allReports.find { it.idTire == id }

        _uiState.update {
            it.copy(
                selectedTire = assemblyTire,
                tireInfo = detailTire,
                cpkReport = report,
                reportLoadState = if (report != null) LoadState.Success(Unit) else LoadState.Error("No se encontró el reporte para esta llanta")
            )
        }
    }

    fun resetReportState() {
        _uiState.update {
            it.copy(
                reportLoadState = LoadState.Idle,
                cpkReport = null,
                selectedTire = null,
                tireInfo = null
            )
        }
    }

    fun resetExportState() {
        _uiState.update { it.copy(exportPdfState = LoadState.Idle, pdfUri = null) }
    }

    fun updatePdfUri(uri: Uri?) {
        _uiState.update {
            it.copy(
                pdfUri = uri,
                exportPdfState = if (uri != null) LoadState.Success(Unit) else LoadState.Error("No se pudo generar el PDF")
            )
        }
    }

    fun sharePdfReport(context: Context, pdfUri: Uri) {
        sharePdf(context, pdfUri)
    }
}

data class CpkUiState(
    val tireList: List<AssemblyTire> = emptyList(),
    val detailTireList: List<Tire> = emptyList(),
    val selectedTire: AssemblyTire? = null,
    val tireInfo: Tire? = null,
    val cpkReport: CpkReportResponse? = null,
    val pdfUri: Uri? = null,
    val menuLoadState: LoadState<Unit> = LoadState.Idle,
    val reportLoadState: LoadState<Unit> = LoadState.Idle,
    val exportPdfState: LoadState<Unit> = LoadState.Idle,
)
