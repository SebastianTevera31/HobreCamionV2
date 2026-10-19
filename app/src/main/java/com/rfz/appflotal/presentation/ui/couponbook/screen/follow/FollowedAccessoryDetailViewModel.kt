package com.rfz.appflotal.presentation.ui.couponbook.screen.follow

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.rfz.appflotal.data.model.promotions.FollowedAccessory
import com.rfz.appflotal.data.repository.promotions.FollowedPromotionsRepository
import com.rfz.appflotal.data.worker.followedAccessoryRefreshWorkName
import com.rfz.appflotal.data.worker.triggerFollowedAccessoryRefresh
import com.rfz.appflotal.presentation.ui.couponbook.navigation.FollowedAccessoryDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

sealed interface FollowedAccessoryDetailUiState {
    data object Loading : FollowedAccessoryDetailUiState

    /** Ya no se sigue (p. ej. se abrió desde una notificación vieja). */
    data object NotFound : FollowedAccessoryDetailUiState
    data class Found(val accessory: FollowedAccessory) : FollowedAccessoryDetailUiState
}

/** Estado de "Buscar ahora". */
enum class RefreshStatus {
    IDLE,

    /** En cola: espera conexión o a que termine otra búsqueda. */
    WAITING,
    SEARCHING,
    FAILED
}

/**
 * [requestedHere]: el usuario tocó "Buscar ahora" en esta visita a la pantalla; una falla
 * de una visita anterior no se muestra.
 */
internal fun refreshStatusFor(state: WorkInfo.State?, requestedHere: Boolean): RefreshStatus =
    when (state) {
        WorkInfo.State.ENQUEUED, WorkInfo.State.BLOCKED -> RefreshStatus.WAITING
        WorkInfo.State.RUNNING -> RefreshStatus.SEARCHING
        WorkInfo.State.FAILED -> if (requestedHere) RefreshStatus.FAILED else RefreshStatus.IDLE
        WorkInfo.State.SUCCEEDED, WorkInfo.State.CANCELLED, null -> RefreshStatus.IDLE
    }

@HiltViewModel
class FollowedAccessoryDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    followedPromotionsRepository: FollowedPromotionsRepository,
    workManager: WorkManager,
    @param:ApplicationContext private val context: Context
) : ViewModel() {

    private val followedAccessoryId =
        savedStateHandle.toRoute<FollowedAccessoryDetail>().followedAccessoryId

    private val refreshRequested = MutableStateFlow(false)

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

    val refreshStatus: StateFlow<RefreshStatus> = combine(
        workManager.getWorkInfosForUniqueWorkFlow(
            followedAccessoryRefreshWorkName(followedAccessoryId)
        ),
        refreshRequested
    ) { infos, requested ->
        refreshStatusFor(infos.firstOrNull()?.state, requested)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = RefreshStatus.IDLE
    )

    fun onRefresh() {
        refreshRequested.value = true
        triggerFollowedAccessoryRefresh(context, followedAccessoryId)
    }
}
