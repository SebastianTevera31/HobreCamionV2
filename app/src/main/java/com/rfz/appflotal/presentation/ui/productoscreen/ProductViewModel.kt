package com.rfz.appflotal.presentation.ui.productoscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfz.appflotal.data.model.product.dto.ProductCrudDto
import com.rfz.appflotal.data.model.product.response.ProductResponse
import com.rfz.appflotal.data.model.originaldesign.response.OriginalDesignResponse
import com.rfz.appflotal.data.model.tire.response.TireSizeResponse
import com.rfz.appflotal.data.model.tire.response.LoadingCapacityResponse
import com.rfz.appflotal.domain.product.*
import com.rfz.appflotal.domain.originaldesign.OriginalDesignUseCase
import com.rfz.appflotal.domain.tire.TireSizeUseCase
import com.rfz.appflotal.domain.tire.LoadingCapacityUseCase
import com.rfz.appflotal.presentation.ui.utils.OperationStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProductUiState(
    val products: List<ProductResponse> = emptyList(),
    val filteredProducts: List<ProductResponse> = emptyList(),
    val designs: List<OriginalDesignResponse> = emptyList(),
    val sizes: List<TireSizeResponse> = emptyList(),
    val capacities: List<LoadingCapacityResponse> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val searchQuery: String = "",
    val operationStatus: OperationStatus = OperationStatus.Idle
)

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val productListUseCase: ProductListUseCase,
    private val productCrudUseCase: ProductCrudUseCase,
    private val productByIdUseCase: ProductByIdUseCase,
    private val originalDesignUseCase: OriginalDesignUseCase,
    private val tireSizeUseCase: TireSizeUseCase,
    private val loadingCapacityUseCase: LoadingCapacityUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductUiState())
    val uiState = _uiState.asStateFlow()

    fun loadInitialData(token: String, userId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val bearerToken = "Bearer $token"
                val productsResult = productListUseCase(bearerToken)
                val designsResult = originalDesignUseCase(bearerToken)
                val sizesResult = tireSizeUseCase.doTireSizes(userId, bearerToken)
                val capacitiesResult = loadingCapacityUseCase.doCapacity(userId, bearerToken)

                _uiState.update { current ->
                    current.copy(
                        products = productsResult.getOrNull() ?: emptyList(),
                        designs = designsResult.getOrNull() ?: emptyList(),
                        sizes = if (sizesResult.isSuccessful) sizesResult.body() ?: emptyList() else emptyList(),
                        capacities = if (capacitiesResult.isSuccessful) capacitiesResult.body() ?: emptyList() else emptyList(),
                        isLoading = false
                    )
                }
                applyFilter()
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
        val all = _uiState.value.products
        val filtered = if (query.isBlank()) {
            all
        } else {
            all.filter { it.descriptionProduct.contains(query, ignoreCase = true) }
        }
        _uiState.update { it.copy(filteredProducts = filtered) }
    }

    fun saveProduct(token: String, dto: ProductCrudDto) {
        viewModelScope.launch {
            _uiState.update { it.copy(operationStatus = OperationStatus.Loading) }
            val result = productCrudUseCase(dto, "Bearer $token")
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
