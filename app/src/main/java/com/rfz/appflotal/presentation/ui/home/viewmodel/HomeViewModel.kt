package com.rfz.appflotal.presentation.ui.home.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfz.appflotal.core.util.AppLocale
import com.rfz.appflotal.data.model.flotalSoft.AppHCEntity
import com.rfz.appflotal.data.model.languaje.LanguageResponse
import com.rfz.appflotal.data.repository.database.HombreCamionRepository
import com.rfz.appflotal.domain.languaje.LanguajeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val hombreCamionRepository: HombreCamionRepository,
    private val languageUseCase: LanguajeUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _homeCheckInMessage = MutableLiveData<String>()
    val homeCheckInMessage: LiveData<String> = _homeCheckInMessage

    suspend fun logout() {
        hombreCamionRepository.clearUserData()
    }

    fun loadInitialData() {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                val deferredUserData = async { hombreCamionRepository.getUserData() }
                val deferredLanguage = async { hombreCamionRepository.getSavedLanguage() ?: "en" }

                _uiState.update {
                    it.copy(
                        userData = deferredUserData.await(),
                        selectedLanguage = deferredLanguage.await(),
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
                _homeCheckInMessage.value = "Error loading data: ${e.message}"
            }
        }
    }

    suspend fun changeLanguage(newLanguage: String): Result<LanguageResponse> {
        return if (newLanguage != uiState.value.selectedLanguage && (newLanguage == "en" || newLanguage == "es")) {
            try {
                _uiState.update { it.copy(isLoading = true) }

                val result = uiState.value.userData?.let { user ->
                    languageUseCase("Bearer ${user.fld_token}", newLanguage)
                } ?: throw Exception("User data not available")

                if (result.isSuccess) {
                    hombreCamionRepository.saveSelectedLanguage(newLanguage)
                    _uiState.update { it.copy(selectedLanguage = newLanguage, isLoading = false) }

                    // Actualiza el Locale global y guarda en SharedPreferences
                    AppLocale.setLocale(Locale(newLanguage))
                    context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
                        .edit()
                        .putString("app_language", newLanguage)
                        .apply()
                }

                result
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
                _homeCheckInMessage.value = "Error changing language: ${e.message}"
                Result.failure(e)
            }
        } else {
            Result.failure(Exception("Invalid language selection"))
        }
    }

    data class HomeUiState(
        val userData: AppHCEntity? = null,
        val selectedLanguage: String = "en",
        val isLoading: Boolean = false
    )
}