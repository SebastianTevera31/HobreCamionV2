package com.rfz.appflotal.presentation.ui.couponbook

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfz.appflotal.R
import com.rfz.appflotal.data.model.couponbook.Coupon
import com.rfz.appflotal.data.model.couponbook.ValidatedVoucher
import com.rfz.appflotal.data.model.couponbook.VoucherStatusType
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
    val filteredCoupons: List<Coupon> = emptyList(),
    val coupons: List<Coupon> = emptyList(),
    val selectedCoupon: Coupon? = null,
    val loadingScreen: LoadState<Unit> = LoadState.Idle,
    val validateState: LoadState<ValidatedVoucher> = LoadState.Idle
)

@HiltViewModel
class CouponBookViewModel @Inject constructor(
    private val couponBookRepository: CouponBookRepository
) : ViewModel() {
    private var _uiState = MutableStateFlow(CouponBookUiState())
    val uiState = _uiState.asStateFlow()

    fun getInitialData() {
        viewModelScope.launch {
            _uiState.update { currentUiState ->
                currentUiState.copy(
                    loadingScreen = LoadState.Loading
                )
            }
            couponBookRepository.getVouchers().fold(
                onSuccess = { vouchers ->
                    _uiState.update { currentUiState ->
                        currentUiState.copy(
                            coupons = vouchers,
                            loadingScreen = LoadState.Success(Unit)
                        )
                    }
                    applyFilters()
                },
                onFailure = {
                    _uiState.update { currentUiState ->
                        currentUiState.copy(
                            loadingScreen = LoadState.Error("Unit")
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
                            coupon.fldCode.contains(
                                currentUiState.searchQuery,
                                ignoreCase = true
                            ) ||
                            coupon.fldTitle.contains(currentUiState.searchQuery, ignoreCase = true)
                }

                val matchesCategory = when (currentUiState.selectedFilter) {
                    CouponFilterOptions.ALL -> true
                    CouponFilterOptions.VALID -> coupon.fldStatus == VoucherStatusType.VALIDO
                    CouponFilterOptions.USED -> coupon.fldStatus == VoucherStatusType.RECLAMADO
                    CouponFilterOptions.EXPIRED -> coupon.fldStatus == VoucherStatusType.EXPIRADO
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
        _uiState.update { currentUiState ->
            currentUiState.copy(
                validateState = LoadState.Loading
            )
        }
        viewModelScope.launch {
            couponBookRepository.validateVoucher(
                code = code
            ).fold(
                onSuccess = { result ->
                    if (!result.used) {
                        _uiState.update { currentUiState ->
                            currentUiState.copy(
                                validateState = LoadState.Success(result)
                            )
                        }
                    } else {
                        _uiState.update { currentUiState ->
                            currentUiState.copy(
                                validateState = LoadState.Error("Unit")
                            )
                        }
                    }
                },
                onFailure = {
                    _uiState.update { currentUiState ->
                        currentUiState.copy(
                            validateState = LoadState.Error("Unit")
                        )
                    }
                }
            )
        }
    }
}