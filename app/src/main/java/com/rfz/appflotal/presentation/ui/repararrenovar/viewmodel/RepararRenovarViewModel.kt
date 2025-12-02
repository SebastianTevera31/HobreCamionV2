package com.rfz.appflotal.presentation.ui.repararrenovar.viewmodel

import androidx.lifecycle.ViewModel
import com.rfz.appflotal.domain.tire.TireGetUseCase
import com.rfz.appflotal.domain.tire.TireListUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class RepararRenovarViewModel @Inject constructor(
    private val tireUseCase: TireListUsecase,
    private val tireGetUseCase: TireGetUseCase
) : ViewModel() {
    private var _uiState = MutableStateFlow(RepararRenovarUiState())
    val uiState = _uiState.asStateFlow()

    fun loadData() {

    }

    fun updateSelectedTire(catalogItemId: Int) {

    }

    fun cleanOperationStatus() {
        _uiState.update { currentUiState ->
            currentUiState.copy(
                operationState = null
            )
        }
    }

    fun cleanUiState() {
        _uiState.value = RepararRenovarUiState()
    }

}