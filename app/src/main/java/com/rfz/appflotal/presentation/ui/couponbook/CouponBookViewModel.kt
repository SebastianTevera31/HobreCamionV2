package com.rfz.appflotal.presentation.ui.couponbook

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfz.appflotal.R
import com.rfz.appflotal.data.model.couponbook.Coupons
import com.rfz.appflotal.data.model.couponbook.toDomain
import com.rfz.appflotal.data.repository.couponbook.CouponBookRepository
import com.rfz.appflotal.presentation.ui.utils.LoadState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class CouponFilterOptions(val text: Int) {
    ALL(R.string.todos),
    VALID(R.string.vigentes),
    USED(R.string.usados),
    EXPIRED(R.string.expirados)
}

data class CouponBookUiState(
    val filterOptions: List<CouponFilterOptions> = CouponFilterOptions.entries,
    val selectedFilter: CouponFilterOptions = CouponFilterOptions.ALL,
    val searchQuery: String = "",
    val filteredCoupons: List<Coupons> = emptyList(),
    val coupons: List<Coupons> = emptyList(),
    val selectedCoupon: Coupons? = null,
    val loadingScreen: LoadState<Unit> = LoadState.Idle
)

@HiltViewModel
class CouponBookViewModel @Inject constructor(
    private val couponBookRepository: CouponBookRepository
) : ViewModel() {
    private var _uiState = MutableStateFlow(CouponBookUiState())
    val uiState = _uiState.asStateFlow()

    fun getInitialData(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            couponBookRepository.getVoucher().fold(
                onSuccess = {
                    _uiState.update { currentUiState ->
                        currentUiState.copy(
                            coupons = it.map { coupon -> coupon.toDomain() },
                            loadingScreen = LoadState.Success(Unit)
                        )
                    }
                    applyFilters()
                },
                onFailure = {
                    _uiState.update { currentUiState ->
                        currentUiState.copy(
                            loadingScreen = LoadState.Error("No se pudo obtener los cupones.")
                        )
                    }
                }
            )
        }
    }

    fun selectFilterOption(option: CouponFilterOptions) {
        _uiState.update { currentUiState ->
            currentUiState.copy(
                selectedFilter = option
            )
        }
        applyFilters()
    }

    fun clearFilterSearch() {
        _uiState.update { currentUiState ->
            currentUiState.copy(
                selectedFilter = CouponFilterOptions.ALL,
                searchQuery = ""
            )
        }
        applyFilters()
    }

    fun onSearchChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        applyFilters()
    }

    private fun applyFilters() {
        _uiState.update { currentUiState ->
            val filteredList = currentUiState.coupons.filter { coupon ->
                val matchesSearch = if (currentUiState.searchQuery.isEmpty()) {
                    true
                } else {
                    coupon.fldDescription.contains(currentUiState.searchQuery, ignoreCase = true) ||
                            coupon.fldCode.contains(currentUiState.searchQuery, ignoreCase = true) ||
                            coupon.fldTitle.contains(currentUiState.searchQuery, ignoreCase = true)
                }

                val matchesCategory = when (currentUiState.selectedFilter) {
                    CouponFilterOptions.ALL -> true
                    CouponFilterOptions.VALID -> coupon.fldStatus == 5
                    CouponFilterOptions.USED -> coupon.fldStatus == 1
                    CouponFilterOptions.EXPIRED -> coupon.fldStatus == 4
                }

                matchesSearch && matchesCategory
            }
            currentUiState.copy(filteredCoupons = filteredList)
        }
    }

    fun selectCoupon(idCoupon: String) {
        _uiState.update { currentUiState ->
            currentUiState.copy(
                selectedCoupon = currentUiState.coupons.find { it.fldCode == idCoupon }
            )
        }
    }

    fun validateVoucher(code: String) {
        viewModelScope.launch {
            couponBookRepository.acquireVoucher(
                code = code
            ).fold(
                onSuccess = {
                    _uiState.update { currentUiState ->
                        currentUiState.copy(
                            loadingScreen = LoadState.Success(Unit)
                        )
                    }
                },
                onFailure = {
                    _uiState.update { currentUiState ->
                        currentUiState.copy(
                            loadingScreen = LoadState.Error("No se pudo obtener el cupon.")
                        )
                    }
                }
            )
        }
    }
}