package com.rfz.appflotal.presentation.ui.marcarenovados.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfz.appflotal.domain.retreadbrand.RetreadBrand
import com.rfz.appflotal.domain.retreadbrand.RetreadBrandCrudUseCase
import com.rfz.appflotal.domain.retreadbrand.RetreadBrandListUseCase
import com.rfz.appflotal.presentation.ui.common.viewmodel.ListManagementUiState
import com.rfz.appflotal.presentation.ui.common.viewmodel.ListManagementViewModel
import com.rfz.appflotal.presentation.ui.utils.responseHelperWithResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MarcaRenovadosViewModel @Inject constructor(
    private val retreadBrandCrudUseCase: RetreadBrandCrudUseCase,
    private val retreadBrandListUseCase: RetreadBrandListUseCase
) : ViewModel(), ListManagementViewModel<RetreadBrand> {

    private var _uiState = MutableStateFlow(ListManagementUiState<RetreadBrand>())
    override val uiState: StateFlow<ListManagementUiState<RetreadBrand>> =
        _uiState.asStateFlow()

    override fun loadItems() {
        viewModelScope.launch {
            val result = retreadBrandListUseCase.invoke()
            responseHelperWithResult(result) { response ->
                _uiState.update { currentUiState ->
                    currentUiState.copy(
                        items = response.map { it.toDomain() },
                        isLoading = false
                    )
                }
            }
        }
    }

    override fun onSearchQueryChanged(query: String) {

    }

    override fun onShowDialog() {

    }

    override fun onDismissDialog() {

    }

    override fun onSaveItem() {

    }

    override fun onDialogFieldChanged(field: String, value: Any) {

    }

}