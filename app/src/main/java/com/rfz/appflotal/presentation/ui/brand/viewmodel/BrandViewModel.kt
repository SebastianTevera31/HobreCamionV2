package com.rfz.appflotal.presentation.ui.brand.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfz.appflotal.data.model.brand.dto.BrandCrudDto
import com.rfz.appflotal.data.model.brand.response.BranListResponse
import com.rfz.appflotal.domain.brand.BrandCrudUseCase
import com.rfz.appflotal.domain.brand.BrandListUseCase
import com.rfz.appflotal.presentation.ui.utils.OperationStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BrandUiState(
    val brands: List<BranListResponse> = emptyList(),
    val filteredBrands: List<BranListResponse> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val searchQuery: String = "",
    val operationStatus: OperationStatus = OperationStatus.Idle
)

@HiltViewModel
class BrandViewModel @Inject constructor(
    private val brandListUseCase: BrandListUseCase,
    private val brandCrudUseCase: BrandCrudUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(BrandUiState())
    val uiState = _uiState.asStateFlow()

    fun loadBrands(token: String, idUser: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val result = brandListUseCase("Bearer $token", idUser)
                if (result.isSuccess) {
                    val brands = result.getOrNull() ?: emptyList()
                    _uiState.update { 
                        it.copy(
                            brands = brands,
                            isLoading = false
                        )
                    }
                    applyFilter()
                } else {
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            errorMessage = result.exceptionOrNull()?.message ?: "Error al cargar marcas"
                        ) 
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        applyFilter()
    }

    private fun applyFilter() {
        val query = _uiState.value.searchQuery
        val allBrands = _uiState.value.brands
        val filtered = if (query.isBlank()) {
            allBrands
        } else {
            allBrands.filter { it.description.contains(query, ignoreCase = true) }
        }
        _uiState.update { it.copy(filteredBrands = filtered) }
    }

    fun saveBrand(token: String, name: String, editingBrand: BranListResponse?) {
        viewModelScope.launch {
            _uiState.update { it.copy(operationStatus = OperationStatus.Loading) }
            val dto = if (editingBrand == null) {
                BrandCrudDto(0, name)
            } else {
                BrandCrudDto(editingBrand.idBrand, name)
            }

            val result = brandCrudUseCase(dto, "Bearer $token")
            if (result.isSuccess) {
                _uiState.update { it.copy(operationStatus = OperationStatus.Success) }
            } else {
                _uiState.update { 
                    it.copy(
                        operationStatus = OperationStatus.Error,
                        errorMessage = result.exceptionOrNull()?.message ?: "Error al guardar"
                    ) 
                }
            }
        }
    }
    
    fun resetOperationStatus() {
        _uiState.update { it.copy(operationStatus = OperationStatus.Idle) }
    }
}
