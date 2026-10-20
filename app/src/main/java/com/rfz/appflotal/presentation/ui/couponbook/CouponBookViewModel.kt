package com.rfz.appflotal.presentation.ui.couponbook

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfz.appflotal.R
import com.rfz.appflotal.data.model.couponbook.Coupon
import com.rfz.appflotal.data.model.couponbook.ValidatedVoucher
import com.rfz.appflotal.data.model.couponbook.VoucherStatusType
import com.rfz.appflotal.data.model.promotions.PromotionCategory
import com.rfz.appflotal.data.model.promotions.StoreDiscount
import com.rfz.appflotal.data.repository.couponbook.CouponBookRepository
import com.rfz.appflotal.data.repository.promotions.PromotionsRepository
import com.rfz.appflotal.presentation.ui.utils.LoadState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

private const val PROMOTIONS_PAGE_SIZE = 10

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
    val acquireState: LoadState<Unit> = LoadState.Idle,
    val promotions: List<StoreDiscount> = emptyList(),
    val promotionsSearchQuery: String = "",
    val promotionStores: List<String> = emptyList(),
    /** Tienda que delimita la búsqueda de promociones; vacío = todas. */
    val selectedPromotionStore: String = "",
    /** Pestaña de la lista de promociones; se conserva al volver a la lista. */
    val selectedPromotionCategory: PromotionCategory = PromotionCategory.ACCESSORIES,
    /**
     * Categoría que se aplica al consultar: la pestaña en la lista de promociones, null en
     * la búsqueda de la pantalla principal (todas las categorías).
     */
    val promotionCategoryFilter: PromotionCategory? = null,
    val selectedPromotion: StoreDiscount? = null,
    val promotionsState: LoadState<Unit> = LoadState.Idle,
    val promotionsPage: Int = 0,
    val promotionsHasNextPage: Boolean = true
)

@HiltViewModel
class CouponBookViewModel @Inject constructor(
    private val couponBookRepository: CouponBookRepository,
    private val promotionsRepository: PromotionsRepository
) : ViewModel() {
    private var _uiState = MutableStateFlow(CouponBookUiState())
    val uiState = _uiState.asStateFlow()

    private val _promotionsQuery = MutableStateFlow("")
    private var promotionsJob: Job? = null

    init {
        observePromotionsSearch()
    }

    @OptIn(FlowPreview::class)
    private fun observePromotionsSearch() {
        viewModelScope.launch {
            _promotionsQuery
                .debounce(600.milliseconds)
                .distinctUntilChanged()
                .collectLatest { goToPromotionsPage(1) }
        }
    }

    fun onPromotionsSearchChanged(query: String) {
        _uiState.update { it.copy(promotionsSearchQuery = query) }
        _promotionsQuery.value = query
    }

    fun onMainSearchChanged(query: String) {
        onSearchChanged(query)
        onPromotionsSearchChanged(query)
    }

    /** Al entrar a la lista de promociones: filtra por la pestaña seleccionada. */
    fun openPromotionList() {
        _uiState.update { it.copy(promotionCategoryFilter = it.selectedPromotionCategory) }
        loadPromotionStores()
        // Forzado: puede venir de la búsqueda principal con otra consulta en curso.
        loadPromotionsPage(1)
    }

    fun onPromotionCategorySelected(category: PromotionCategory) {
        if (category == _uiState.value.promotionCategoryFilter) return
        // Las tiendas cambian con la categoría; la elegida podría no tener nada en la nueva.
        _uiState.update {
            it.copy(
                selectedPromotionCategory = category,
                promotionCategoryFilter = category,
                selectedPromotionStore = ""
            )
        }
        loadPromotionStores()
        loadPromotionsPage(1)
    }

    private fun loadPromotionStores() {
        val category = _uiState.value.promotionCategoryFilter
        viewModelScope.launch {
            promotionsRepository.getStores(category).onSuccess { stores ->
                // Descarta la respuesta si mientras tanto se cambió de pestaña.
                _uiState.update {
                    if (it.promotionCategoryFilter == category) it.copy(promotionStores = stores) else it
                }
            }
        }
    }

    fun onPromotionStoreSelected(store: String) {
        if (store == _uiState.value.selectedPromotionStore) return
        _uiState.update { it.copy(selectedPromotionStore = store) }
        // Un cambio de filtro debe recargar aunque haya una consulta en curso,
        // por eso no pasa por la guarda de goToPromotionsPage.
        loadPromotionsPage(1)
    }

    fun goToPromotionsPage(page: Int) {
        val state = _uiState.value
        if (state.promotionsState is LoadState.Loading || page < 1) return
        if (page > state.promotionsPage && !state.promotionsHasNextPage) return

        loadPromotionsPage(page)
    }

    private fun loadPromotionsPage(page: Int) {
        promotionsJob?.cancel()
        _uiState.update { it.copy(promotionsState = LoadState.Loading) }
        val state = _uiState.value

        promotionsJob = viewModelScope.launch {
            promotionsRepository.getPromotionsPage(
                page = page,
                pageSize = PROMOTIONS_PAGE_SIZE,
                search = state.promotionsSearchQuery,
                store = state.selectedPromotionStore,
                category = state.promotionCategoryFilter
            ).fold(
                onSuccess = { pageResult ->
                    _uiState.update {
                        it.copy(
                            promotions = pageResult.items,
                            promotionsPage = page,
                            promotionsHasNextPage = pageResult.hasNextPage,
                            promotionsState = LoadState.Success(Unit)
                        )
                    }
                },
                onFailure = {
                    _uiState.update {
                        it.copy(promotionsState = LoadState.Error("Error al consultar promociones"))
                    }
                }
            )
        }
    }

    fun selectPromotion(productUrl: String) {
        _uiState.update { currentUiState ->
            currentUiState.copy(
                selectedPromotion = currentUiState.promotions.find { it.productUrl == productUrl }
            )
        }
    }

    /** Para promociones que no vienen de la lista paginada (p. ej. las de "Siguiendo"). */
    fun selectPromotion(discount: StoreDiscount) {
        _uiState.update { it.copy(selectedPromotion = discount) }
    }

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
                promotionsSearchQuery = "",
                selectedPromotionStore = "",
                // Pantalla principal: la búsqueda abarca todas las categorías.
                promotionCategoryFilter = null,
                validateState = LoadState.Idle
            )
        }
        _promotionsQuery.value = ""
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