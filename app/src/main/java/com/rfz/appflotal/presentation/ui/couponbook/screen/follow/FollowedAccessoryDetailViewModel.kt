package com.rfz.appflotal.presentation.ui.couponbook.screen.follow

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.rfz.appflotal.data.model.promotions.FollowedAccessory
import com.rfz.appflotal.data.repository.promotions.FollowedPromotionsRepository
import com.rfz.appflotal.data.worker.triggerFollowedPromotionsSync
import com.rfz.appflotal.presentation.ui.couponbook.navigation.FollowedAccessoryDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

sealed interface FollowedAccessoryDetailUiState {
    data object Loading : FollowedAccessoryDetailUiState

    /** Ya no se sigue (p. ej. se abrió desde una notificación vieja). */
    data object NotFound : FollowedAccessoryDetailUiState
    data class Found(val accessory: FollowedAccessory) : FollowedAccessoryDetailUiState
}

@HiltViewModel
class FollowedAccessoryDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    followedPromotionsRepository: FollowedPromotionsRepository,
    @param:ApplicationContext private val context: Context
) : ViewModel() {

    private val followedAccessoryId =
        savedStateHandle.toRoute<FollowedAccessoryDetail>().followedAccessoryId

    val uiState: StateFlow<FollowedAccessoryDetailUiState> = followedPromotionsRepository
        .observeFollowedAccessory(followedAccessoryId)
        .map { accessory ->
            accessory?.let { FollowedAccessoryDetailUiState.Found(it) }
                ?: FollowedAccessoryDetailUiState.NotFound
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = FollowedAccessoryDetailUiState.Loading
        )

    fun onRefresh() {
        triggerFollowedPromotionsSync(context)
    }
}
