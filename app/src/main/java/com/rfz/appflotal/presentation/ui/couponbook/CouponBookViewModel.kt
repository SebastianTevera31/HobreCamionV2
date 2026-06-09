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
import kotlinx.coroutines.async
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
    val filteredVouchers: List<Coupon> = emptyList(),
    val coupons: List<Coupon> = emptyList(),
    val vouchers: List<Coupon> = emptyList(),
    val selectedCoupon: Coupon? = null,
    val voucherId: Int = 0,
    val loadingScreen: LoadState<Unit> = LoadState.Idle,
    val validateState: LoadState<ValidatedVoucher> = LoadState.Idle,
    val acquireState: LoadState<Unit> = LoadState.Idle
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

            val vouchersResultDeferred = async { couponBookRepository.getVouchers() }
            val couponsResultDeferred = async { couponBookRepository.getCoupons() }

            val vouchersResult = vouchersResultDeferred.await()
            val couponsResult = couponsResultDeferred.await()

            if (vouchersResult.isSuccess && couponsResult.isSuccess) {
                _uiState.update { currentUiState ->
                    currentUiState.copy(
                        vouchers = vouchersResult.getOrNull() ?: emptyList(),
                        coupons = couponsResult.getOrNull() ?: emptyList(),
                        loadingScreen = LoadState.Success(Unit)
                    )
                }
                applyFilters()
            } else {
                _uiState.update { currentUiState ->
                    currentUiState.copy(
                        loadingScreen = LoadState.Error("Error")
                    )
                }
            }
        }
    }

    fun selectFilterOption(option: CouponFilterOptions) {
        _uiState.update { currentUiState ->
            currentUiState.copy(
                selectedFilter = option,
                validateState = LoadState.Idle
            )
        }
        applyFilters()
    }

    fun clearFilterSearch() {
        _uiState.update { currentUiState ->
            currentUiState.copy(
                selectedFilter = CouponFilterOptions.ALL,
                searchQuery = "",
                validateState = LoadState.Idle
            )
        }
        applyFilters()
    }

    fun onSearchChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query, validateState = LoadState.Idle) }
        applyFilters()
    }

    private fun applyFilters() {
        _uiState.update { currentUiState ->
            val filterLambda: (Coupon) -> Boolean = { coupon ->
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

            val statusPriority = mapOf(
                VoucherStatusType.VALIDO to 0,
                VoucherStatusType.RECLAMADO to 1,
                VoucherStatusType.EXPIRADO to 2,
                VoucherStatusType.INACTIVO to 3,
                VoucherStatusType.NO_VALIDO to 4
            )

            currentUiState.copy(
                filteredCoupons = currentUiState.coupons
                    .filter(filterLambda)
                    .sortedBy { statusPriority[it.fldStatus] ?: 5 },
                filteredVouchers = currentUiState.vouchers
                    .filter(filterLambda)
                    .sortedBy { statusPriority[it.fldStatus] ?: 5 }
            )
        }
    }

    fun selectCoupon(code: String) {
        _uiState.update { currentUiState ->
            currentUiState.copy(
                selectedCoupon = currentUiState.coupons.find { it.fldCode == code }
                    ?: currentUiState.vouchers.find { it.fldCode == code }
            )
        }
    }

    fun validateVoucher(code: String) {
        // Validación local de expiración
        val voucher = _uiState.value.vouchers.find { it.fldCode == code }
        if (voucher?.fldStatus == VoucherStatusType.EXPIRADO) {
            _uiState.update { it.copy(validateState = LoadState.Error("El cupón ha expirado y no puede ser canjeado")) }
            return
        }

        _uiState.update { it.copy(validateState = LoadState.Loading) }

        viewModelScope.launch {
            couponBookRepository.validateVoucher(code).fold(
                onSuccess = { result ->
                    if (!result.used) {
                        _uiState.update {
                            it.copy(
                                voucherId = result.idVoucher,
                                validateState = LoadState.Success(result)
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(validateState = LoadState.Error("El cupón ya ha sido canjeado"))
                        }
                    }
                },
                onFailure = {
                    _uiState.update {
                        it.copy(validateState = LoadState.Error("Error al validar cupón"))
                    }
                }
            )
        }
    }

    fun acquireVoucher(idCoupon: Int) {
        _uiState.update { it.copy(acquireState = LoadState.Loading) }
        viewModelScope.launch {
            couponBookRepository.acquireVoucher(idCoupon).fold(
                onSuccess = { result ->
                    if (result.id == 200) {
                        val vouchersResult = couponBookRepository.getVouchers()
                        _uiState.update { currentUiState ->
                            currentUiState.copy(
                                vouchers = vouchersResult.getOrNull() ?: currentUiState.vouchers,
                                acquireState = LoadState.Success(Unit)
                            )
                        }
                    } else {
                        _uiState.update { currentUiState ->
                            currentUiState.copy(
                                acquireState = LoadState.Error(
                                    result.message ?: "Error al adquirir cupón"
                                )
                            )
                        }
                    }
                },
                onFailure = { result ->
                    _uiState.update { currentUiState ->
                        currentUiState.copy(
                            acquireState = LoadState.Error(
                                result.message ?: "Error al adquirir cupón"
                            )
                        )
                    }
                }
            )
        }
    }

    fun resetValidateState() {
        _uiState.update { it.copy(validateState = LoadState.Idle, voucherId = 0) }
    }

    fun resetAcquireState() {
        _uiState.update { it.copy(acquireState = LoadState.Idle) }
    }
}