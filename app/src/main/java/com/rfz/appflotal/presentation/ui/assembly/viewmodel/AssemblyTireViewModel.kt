package com.rfz.appflotal.presentation.ui.assembly.viewmodel

import androidx.lifecycle.ViewModel
import com.rfz.appflotal.data.repository.assembly.AssemblyTireRepository
import com.rfz.appflotal.domain.tire.TireListUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AssemblyTireViewModel @Inject constructor(
    private val assemblyTireRepository: AssemblyTireRepository,
    private val tireUseCase: TireListUsecase
) : ViewModel() {
    private val _uiState = MutableStateFlow(AssemblyTireUiState())
    val uiState = _uiState.asStateFlow()

    fun loadDataList() {

    }

    fun registerAssemblyTire() {

    }

    private fun validateAssemblyTireData() {

    }

    private fun cleanUiState() {

    }
}