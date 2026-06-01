package com.rfz.appflotal.presentation.ui.couponbook

import androidx.lifecycle.ViewModel
import com.rfz.appflotal.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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
    val coupons: List<String> = emptyList()
)

@HiltViewModel
class CouponBookViewModel @Inject constructor() : ViewModel() {
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
}