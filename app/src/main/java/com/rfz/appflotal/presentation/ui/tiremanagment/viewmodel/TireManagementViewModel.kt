package com.rfz.appflotal.presentation.ui.tiremanagment.viewmodel

import androidx.lifecycle.ViewModel
import com.rfz.appflotal.data.model.tiremanagement.TireManagementItem
import com.rfz.appflotal.domain.brand.BrandListUseCase
import com.rfz.appflotal.domain.originaldesign.OriginalDesignUseCase
import com.rfz.appflotal.domain.product.ProductListUseCase
import com.rfz.appflotal.domain.tire.TireSizeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class TireManagementViewModel @Inject constructor(
    private val productUseCase: ProductListUseCase,
    private val brandUseCase: BrandListUseCase,
    private val designUseCase: OriginalDesignUseCase,
    private val sizeUseCase: TireSizeUseCase
) : ViewModel() {

    private val _products = MutableStateFlow<List<TireManagementItem>>(emptyList())
    private val _brands = MutableStateFlow<List<TireManagementItem>>(emptyList())
    private val _designs = MutableStateFlow<List<TireManagementItem>>(emptyList())
    private val _sizes = MutableStateFlow<List<TireManagementItem>>(emptyList())


    private val _uiState = MutableStateFlow(TireManagementUiState())
    val uiState = _uiState.asStateFlow()

    fun navigateToScreen(screen: TireManagementDestinations) {
        _uiState.update {
            it.copy(currentScreen = screen)
        }

        when (screen) {
            TireManagementDestinations.Tire -> if (_products.value.isEmpty()) loadProducts()
            TireManagementDestinations.Catalogs -> loadBrands()
        }
    }

    private fun loadBrands() {}
    private fun loadDesignData() {}
    private fun loadSizeData() {}
    private fun loadProducts() {}
}