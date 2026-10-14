package com.rfz.appflotal.presentation.ui.couponbook.screen.follow

import android.content.Context
import android.database.sqlite.SQLiteException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfz.appflotal.data.model.catalog.TopTenAccessoryResponse
import com.rfz.appflotal.data.model.promotions.AccessoryVehicleType
import com.rfz.appflotal.data.model.promotions.FollowedAccessory
import com.rfz.appflotal.data.repository.promotions.FollowValidation
import com.rfz.appflotal.data.repository.promotions.FollowedPromotionsRepository
import com.rfz.appflotal.data.repository.promotions.MAX_FOLLOWED_ACCESSORIES
import com.rfz.appflotal.data.repository.promotions.suggestedAccessoriesFor
import com.rfz.appflotal.data.worker.triggerFollowedPromotionsSync
import com.rfz.appflotal.presentation.ui.utils.LoadState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Error al seguir: una regla de validación o una falla al guardar en la base local. */
sealed interface FollowError {
    data class Invalid(val reason: FollowValidation) : FollowError
    data object SaveFailed : FollowError
}

data class FollowPromotionUiState(
    val selectedType: AccessoryVehicleType = AccessoryVehicleType.TRUCK,
    val followed: List<FollowedAccessory> = emptyList(),
    val topTen: List<TopTenAccessoryResponse> = emptyList(),
    val suggestionsState: LoadState<Unit> = LoadState.Idle,
    val newAccessoryText: String = "",
    val followError: FollowError? = null,
    val maxFollowed: Int = MAX_FOLLOWED_ACCESSORIES
) {
    val followedForTab: List<FollowedAccessory>
        get() = followed.filter { it.vehicleType == selectedType }

    /** Sugeridos de la pestaña que el usuario aún no sigue. */
    val suggestionsForTab: List<TopTenAccessoryResponse>
        get() {
            val followedIds = followed.mapNotNull { it.catalogAccessoryId }.toSet()
            return suggestedAccessoriesFor(topTen, selectedType)
                .filterNot { it.idAccessory in followedIds }
        }

    val isLimitReached: Boolean
        get() = followed.size >= maxFollowed
}

@HiltViewModel
class FollowPromotionViewModel @Inject constructor(
    private val followedPromotionsRepository: FollowedPromotionsRepository,
    @param:ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(FollowPromotionUiState())
    val uiState = _uiState.asStateFlow()

    init {
        observeFollowed()
        selectUserVehicleType()
        loadSuggestions()
    }

    private fun observeFollowed() {
        viewModelScope.launch {
            followedPromotionsRepository.observeFollowedAccessories().collect { followed ->
                _uiState.update { it.copy(followed = followed) }
            }
        }
    }

    private fun selectUserVehicleType() {
        viewModelScope.launch {
            val type = followedPromotionsRepository.getUserVehicleType()
            _uiState.update { it.copy(selectedType = type) }
        }
    }

    fun loadSuggestions() {
        if (_uiState.value.suggestionsState is LoadState.Loading) return
        _uiState.update { it.copy(suggestionsState = LoadState.Loading) }

        viewModelScope.launch {
            followedPromotionsRepository.getSuggestedAccessories().fold(
                onSuccess = { accessories ->
                    _uiState.update {
                        it.copy(topTen = accessories, suggestionsState = LoadState.Success(Unit))
                    }
                },
                onFailure = {
                    // El mensaje visible lo resuelve la pantalla con un recurso de texto.
                    _uiState.update { it.copy(suggestionsState = LoadState.Error("")) }
                }
            )
        }
    }

    fun onTabSelected(type: AccessoryVehicleType) {
        _uiState.update { it.copy(selectedType = type, followError = null) }
    }

    fun onNewAccessoryTextChanged(text: String) {
        _uiState.update { it.copy(newAccessoryText = text, followError = null) }
    }

    fun onAddCustomAccessory() {
        val state = _uiState.value
        follow(name = state.newAccessoryText, clearInputOnSuccess = true)
    }

    fun onFollowSuggestion(accessory: TopTenAccessoryResponse) {
        follow(
            name = accessory.name.orEmpty(),
            catalogAccessoryId = accessory.idAccessory,
            description = accessory.description,
            clearInputOnSuccess = false
        )
    }

    fun onUnfollow(followedAccessoryId: Long) {
        viewModelScope.launch {
            try {
                followedPromotionsRepository.unfollow(followedAccessoryId)
            } catch (_: SQLiteException) {
                _uiState.update { it.copy(followError = FollowError.SaveFailed) }
            }
        }
    }

    fun onRefreshPromotions() {
        triggerFollowedPromotionsSync(context)
    }

    private fun follow(
        name: String,
        catalogAccessoryId: Int? = null,
        description: String? = null,
        clearInputOnSuccess: Boolean
    ) {
        val vehicleType = _uiState.value.selectedType
        viewModelScope.launch {
            val validation = try {
                followedPromotionsRepository.follow(
                    rawName = name,
                    vehicleType = vehicleType,
                    catalogAccessoryId = catalogAccessoryId,
                    description = description
                )
            } catch (_: SQLiteException) {
                _uiState.update { it.copy(followError = FollowError.SaveFailed) }
                return@launch
            }

            if (validation == FollowValidation.VALID) {
                _uiState.update {
                    it.copy(
                        followError = null,
                        newAccessoryText = if (clearInputOnSuccess) "" else it.newAccessoryText
                    )
                }
                // Primera búsqueda del accesorio; no notifica, solo llena la caché.
                triggerFollowedPromotionsSync(context)
            } else {
                _uiState.update { it.copy(followError = FollowError.Invalid(validation)) }
            }
        }
    }
}
