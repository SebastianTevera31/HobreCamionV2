package com.rfz.appflotal.presentation.ui.reportes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfz.appflotal.data.repository.report.ReportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(private val reportRepository: ReportRepository) :
    ViewModel() {
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