package com.rfz.appflotal.presentation.ui.blog.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class BlogViewModel @Inject constructor() : ViewModel() {
    private var _uiState = MutableStateFlow(BlogUiState())
    val uiState = _uiState.asStateFlow()
}