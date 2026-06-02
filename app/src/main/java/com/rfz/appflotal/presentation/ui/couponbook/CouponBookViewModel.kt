package com.rfz.appflotal.presentation.ui.couponbook

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfz.appflotal.R
import com.rfz.appflotal.data.model.couponbook.RedeemDto
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
    val filteredCoupons: List<String> = emptyList(),
    val coupons: List<String> = emptyList(),
    val selectedCoupon: String? = null,
    val loadingScreen: LoadState<Unit> = LoadState.Idle
)

@HiltViewModel
class CouponBookViewModel @Inject constructor(
    private val couponBookRepository: CouponBookRepository
) : ViewModel() {
    private var _uiState = MutableStateFlow(CouponBookUiState())
    val uiState = _uiState.asStateFlow()

    fun getInitialData(forceRefresh: Boolean = false) {

    }

    fun selectFilterOption(option: CouponFilterOptions) {
        _uiState.update { currentUiState ->
            currentUiState.copy(
                selectedFilter = option
            )
        }
    }

    fun clearFilterSearch() {
        _uiState.update { currentUiState ->
            currentUiState.copy(
                filteredCoupons = emptyList(),
                searchQuery = ""
            )
        }
    }

    fun onSearchChanged() {

    }

    fun doRedeem() {
        viewModelScope.launch {
            couponBookRepository.redeemVoucher(
                redeemDto = RedeemDto(
                    code = "",
                    idUserCustomer = 1,
                    idUserCashier = 1,
                    idBusiness = 1,
                    originalAmount = 1
                )
            ).fold(
                onSuccess = {},
                onFailure = {}
            )
        }
    }

    fun validateVoucher() {
        viewModelScope.launch {
            couponBookRepository.validateVoucher(
                code = ""
            ).fold(
                onSuccess = {},
                onFailure = {}
            )
        }
    }
}