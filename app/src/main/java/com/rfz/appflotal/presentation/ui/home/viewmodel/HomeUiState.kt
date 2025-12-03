package com.rfz.appflotal.presentation.ui.home.viewmodel

import com.rfz.appflotal.data.model.database.AppHCEntity
import com.rfz.appflotal.presentation.ui.utils.OperationStatus

data class HomeUiState(
    val userData: AppHCEntity? = null,
    val selectedLanguage: String = "en",
    val isLoading: Boolean = false,
    val screenLoadStatus: OperationStatus = OperationStatus.Loading
)