package com.rfz.appflotal.presentation.ui.couponbook.screen.follow

import android.content.Context
import android.database.sqlite.SQLiteException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfz.appflotal.data.model.catalog.TopTenAccessoryResponse
import com.rfz.appflotal.data.model.promotions.AccessoryVehicleType
import com.rfz.appflotal.data.model.promotions.FollowedAccessory
import com.rfz.appflotal.data.repository.promotions.FollowFilter
import com.rfz.appflotal.data.repository.promotions.FollowSections
import com.rfz.appflotal.data.repository.promotions.FollowValidation
import com.rfz.appflotal.data.repository.promotions.FollowedPromotionsRepository
import com.rfz.appflotal.data.repository.promotions.MAX_FOLLOWED_ACCESSORIES
import com.rfz.appflotal.data.repository.promotions.buildFollowSections
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

/** Mensaje de un solo uso que la pantalla muestra en un snackbar. */
sealed interface FollowMessage {
    data class Error(val error: FollowError) : FollowMessage
    data class Unfollowed(val accessoryName: String) : FollowMessage
}

/** Lo necesario para volver a seguir un accesorio si el usuario toca "Deshacer". */
private data class UnfollowedSnapshot(
    val name: String,
    val vehicleType: AccessoryVehicleType,
    val catalogAccessoryId: Int?,
    val description: String?
)

data class FollowPromotionUiState(
    val selectedType: AccessoryVehicleType = AccessoryVehicleType.TRUCK,
    val filter: FollowFilter = FollowFilter.ALL,
    val followed: List<FollowedAccessory> = emptyList(),
    val topTen: List<TopTenAccessoryResponse> = emptyList(),
    val suggestionsState: LoadState<Unit> = LoadState.Idle,
    val isAddSheetVisible: Boolean = false,
    val newAccessoryText: String = "",
    // Error de la hoja de agregar; se muestra junto al campo.
    val addError: FollowError? = null,
    val message: FollowMessage? = null,
    val maxFollowed: Int = MAX_FOLLOWED_ACCESSORIES
) {
    val sections: FollowSections
        get() = buildFollowSections(
            topTen = topTen,
            topTenLoaded = suggestionsState is LoadState.Success,
            followed = followed,
            vehicleType = selectedType,
            filter = filter
        )

    val followedInTabCount: Int
        get() = followed.count { it.vehicleType == selectedType }
}

@HiltViewModel
class FollowPromotionViewModel @Inject constructor(
    private val followedPromotionsRepository: FollowedPromotionsRepository,
    @param:ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(FollowPromotionUiState())
    val uiState = _uiState.asStateFlow()

    private var lastUnfollowed: UnfollowedSnapshot? = null

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
        _uiState.update { it.copy(selectedType = type) }
    }

    fun onFilterSelected(filter: FollowFilter) {
        _uiState.update { it.copy(filter = filter) }
    }

    fun onShowAddSheet() {
        _uiState.update { it.copy(isAddSheetVisible = true, newAccessoryText = "", addError = null) }
    }

    fun onDismissAddSheet() {
        _uiState.update { it.copy(isAddSheetVisible = false, addError = null) }
    }

    fun onNewAccessoryTextChanged(text: String) {
        _uiState.update { it.copy(newAccessoryText = text, addError = null) }
    }

    fun onAddCustomAccessory() {
        val state = _uiState.value
        viewModelScope.launch {
            when (val result = follow(name = state.newAccessoryText, vehicleType = state.selectedType)) {
                null -> _uiState.update {
                    it.copy(isAddSheetVisible = false, newAccessoryText = "", addError = null)
                }

                else -> _uiState.update { it.copy(addError = result) }
            }
        }
    }

    fun onFollowSuggestion(accessory: TopTenAccessoryResponse) {
        val vehicleType = _uiState.value.selectedType
        viewModelScope.launch {
            follow(
                name = accessory.name.orEmpty(),
                vehicleType = vehicleType,
                catalogAccessoryId = accessory.idAccessory,
                description = accessory.description
            )?.let { error -> showMessage(FollowMessage.Error(error)) }
        }
    }

    /** Se llama desde el detalle; la pantalla principal muestra el snackbar con "Deshacer". */
    fun onUnfollow(accessory: FollowedAccessory) {
        viewModelScope.launch {
            try {
                followedPromotionsRepository.unfollow(accessory.id)
                lastUnfollowed = UnfollowedSnapshot(
                    name = accessory.name,
                    vehicleType = accessory.vehicleType,
                    catalogAccessoryId = accessory.catalogAccessoryId,
                    description = accessory.description
                )
                showMessage(FollowMessage.Unfollowed(accessory.name))
            } catch (_: SQLiteException) {
                showMessage(FollowMessage.Error(FollowError.SaveFailed))
            }
        }
    }

    /** Vuelve a seguir lo último que se dejó de seguir; sus promociones llegan con la nueva búsqueda. */
    fun onUndoUnfollow() {
        val snapshot = lastUnfollowed ?: return
        lastUnfollowed = null
        viewModelScope.launch {
            follow(
                name = snapshot.name,
                vehicleType = snapshot.vehicleType,
                catalogAccessoryId = snapshot.catalogAccessoryId,
                description = snapshot.description
            )?.let { error -> showMessage(FollowMessage.Error(error)) }
        }
    }

    fun onMessageShown() {
        _uiState.update { it.copy(message = null) }
    }

    private fun showMessage(message: FollowMessage) {
        _uiState.update { it.copy(message = message) }
    }

    /** Devuelve null si se siguió; si no, el motivo. */
    private suspend fun follow(
        name: String,
        vehicleType: AccessoryVehicleType,
        catalogAccessoryId: Int? = null,
        description: String? = null
    ): FollowError? {
        val validation = try {
            followedPromotionsRepository.follow(
                rawName = name,
                vehicleType = vehicleType,
                catalogAccessoryId = catalogAccessoryId,
                description = description
            )
        } catch (_: SQLiteException) {
            return FollowError.SaveFailed
        }

        if (validation != FollowValidation.VALID) return FollowError.Invalid(validation)

        // Primera búsqueda del accesorio; no notifica, solo llena la caché.
        triggerFollowedPromotionsSync(context)
        return null
    }
}
