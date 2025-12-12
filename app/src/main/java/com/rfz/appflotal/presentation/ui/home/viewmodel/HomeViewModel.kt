package com.rfz.appflotal.presentation.ui.home.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfz.appflotal.core.util.AppLocale
import com.rfz.appflotal.data.model.apputilities.UserOpinion
import com.rfz.appflotal.data.model.languaje.LanguageResponse
import com.rfz.appflotal.data.repository.AppStatusManagerRepository
import com.rfz.appflotal.data.repository.apputilities.AppUtilitiesRepositoryImpl
import com.rfz.appflotal.data.repository.database.HombreCamionRepository
import com.rfz.appflotal.domain.languaje.LanguajeUseCase
import com.rfz.appflotal.domain.login.LoginUseCase
import com.rfz.appflotal.presentation.ui.inicio.ui.PaymentPlanType
import com.rfz.appflotal.presentation.ui.utils.FireCloudMessagingType
import com.rfz.appflotal.presentation.ui.utils.OperationStatus
import com.rfz.appflotal.presentation.ui.utils.asyncResponseHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val hombreCamionRepository: HombreCamionRepository,
    private val languageUseCase: LanguajeUseCase,
    private val loginUseCase: LoginUseCase,
    private val appUtilitiesRepository: AppUtilitiesRepositoryImpl,
    private val appStatusManagerRepository: AppStatusManagerRepository,
    @param:ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _messageOperationState: MutableStateFlow<OperationStatus> =
        MutableStateFlow(OperationStatus.Loading)
    val messageOperationState = _messageOperationState.asStateFlow()

    private val _homeCheckInMessage = MutableLiveData<String>()
    val homeCheckInMessage: LiveData<String> = _homeCheckInMessage

    val notificationState = appStatusManagerRepository.appState.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000), null
    )

    suspend fun logout() {
        hombreCamionRepository.clearUserData()
        _messageOperationState.value = OperationStatus.Loading
        _uiState.value = HomeUiState()
    }


    fun loadInitialData() {
        _uiState.update { it.copy(isLoading = true) }
        listenToAppStatusChanges()
        //listenUserPlan()
        viewModelScope.launch {
            try {
                val deferredUserData = async { hombreCamionRepository.getUserData() }
                val deferredLanguage = async {
                    hombreCamionRepository.getSavedLanguage()
                        ?: AppLocale.currentLocale.value.language
                }
                val user = deferredUserData.await()
                val language = deferredLanguage.await()

                if (user != null) {
                    languageUseCase("Bearer ${user.fld_token}", language)
                    // Notificar a la API el idioma de la app
                    changeLanguage(AppLocale.currentLocale.value.language)
                    _uiState.update {
                        it.copy(
                            userData = user,
                            selectedLanguage = language,
                            isLoading = false,
                            screenLoadStatus = OperationStatus.Success,
                            paymentPlanType = PaymentPlanType.valueOf(
                                user.paymentPlan.replace(
                                    " ",
                                    ""
                                )
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        screenLoadStatus = OperationStatus.Error
                    )
                }
                _homeCheckInMessage.value = "Error loading data: ${e.message}"
            }
        }
    }

    private fun listenToAppStatusChanges() {
        viewModelScope.launch {
            appStatusManagerRepository.appState.collect { notificationState ->
                when (notificationState.eventType) {
                    FireCloudMessagingType.CAMBIO_DE_PLAN -> {
                        _uiState.update { it.copy(screenLoadStatus = OperationStatus.Loading) }
                    }

                    FireCloudMessagingType.TERMINOS -> {
                        _uiState.update { it.copy(showTermsAndConditions = true) }
                    }

                    else -> {
                        _uiState.update { it.copy(showTermsAndConditions = false) }
                    }
                }
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
                val response = result.getOrNull()
                val idUser = _uiState.value.userData
                if (response != null && idUser != null) {
                    hombreCamionRepository.updateToken(
                        idUser.idUser,
                        response.token
                    )
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

    fun onSendFeedback(feedback: String) {
        viewModelScope.launch {
            val result = appUtilitiesRepository.sendFeedback(
                UserOpinion(
                    opinion = feedback,
                    registerDate = OffsetDateTime.now()
                )
            )

            result.onSuccess {
                _messageOperationState.value = OperationStatus.Success
            }.onFailure {
                _messageOperationState.value = OperationStatus.Error
            }
        }
    }

    fun acceptNewTermsAndConditions() {
        viewModelScope.launch {
            val result = loginUseCase.doAcceptTermsAndConditions()
            asyncResponseHelper(result) {
                appStatusManagerRepository.cleanNotificationsState()
            }
        }
    }

    fun cleanOperationStatus() {
        _messageOperationState.value = OperationStatus.Loading
    }
}
