package com.rfz.appflotal.presentation.ui.dissassembly.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfz.appflotal.domain.axle.GetAxlesUseCase
import com.rfz.appflotal.domain.database.GetTasksUseCase
import com.rfz.appflotal.domain.disassembly.DisassemblyCauseUseCase
import com.rfz.appflotal.domain.tire.TireGetUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DisassemblyViewModel @Inject constructor(
    private val getAxlesUseCase: GetAxlesUseCase,
    private val disassemblyCauseUseCase: DisassemblyCauseUseCase,
    private val getTireUseCase: TireGetUseCase,
    private val getTasksUseCase: GetTasksUseCase
) :
    ViewModel() {
    private var _uiState = MutableStateFlow(DisassemblyUiState())
    val uiState: StateFlow<DisassemblyUiState> = _uiState.asStateFlow()

    fun loadData(tirePosition: String, tireId: Int) {
        viewModelScope.launch {
            val token = getTasksUseCase().first().first().fld_token

            val getAxlesDeferred = async { getAxlesUseCase() }
            val getTireDeferred = async { getTireUseCase(tireId, token) }
            val getDisassemblyCauseDeferred = async { disassemblyCauseUseCase() }

            val axles = getAxlesDeferred.await()
            val tire = getTireDeferred.await()
            val disassemblyCauses = getDisassemblyCauseDeferred.await()
        }
    }

}

