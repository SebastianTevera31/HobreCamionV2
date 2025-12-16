package com.rfz.appflotal.presentation.ui.inicio.viewmodel

import android.content.Context
import android.util.Log
import android.view.ViewGroup
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.rfz.appflotal.data.model.database.AppHCEntity
import com.rfz.appflotal.domain.database.DeleteTasksUseCase
import com.rfz.appflotal.domain.database.GetTasksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InicioScreenViewModel @Inject constructor(
    private val getTasksUseCase: GetTasksUseCase,
    private val deleteTasksUseCase: DeleteTasksUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainActivityUiState())
    val uiState = _uiState.asStateFlow()

    init {
        checkUserSession()
    }

    private fun checkUserSession() {
        viewModelScope.launch {
            try {
                val tasks = getTasksUseCase().first()
                _uiState.update { currentUiState ->
                    currentUiState.copy(
                        userData = tasks.firstOrNull(),
                        initialValidationCompleted = true
                    )
                }
            } catch (_: Exception) {
                _uiState.update { currentUiState ->
                    currentUiState.copy(
                        userData = null,
                        initialValidationCompleted = true
                    )
                }
            }
        }
    }

    fun deleteUserData() {
        viewModelScope.launch {
            try {
                deleteTasksUseCase()
                _uiState.update { currentUiState ->
                    currentUiState.copy(
                        userData = null
                    )
                }
            } catch (_: Exception) {

            }
        }
    }
}

data class MainActivityUiState(
    val adView: AdView? = null,
    val userData: AppHCEntity? = null,
    val initialValidationCompleted: Boolean = false
)