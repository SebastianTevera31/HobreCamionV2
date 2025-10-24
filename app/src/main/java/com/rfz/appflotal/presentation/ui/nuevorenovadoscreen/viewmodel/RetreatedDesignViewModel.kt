package com.rfz.appflotal.presentation.ui.nuevorenovadoscreen.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfz.appflotal.domain.originaldesign.CrudOriginalDesignUseCase
import com.rfz.appflotal.domain.retreaddesign.RetreadDesignListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RetreatedDesignViewModel @Inject constructor(
    private val crudRetreatedDesign: CrudOriginalDesignUseCase,
    private val retreatedDesignUseCase: RetreadDesignListUseCase
) : ViewModel() {
    private var _retreatedDesignList = MutableStateFlow(RetreatedDesignUiState())


    fun getRetreatedDesigns() {
        viewModelScope.launch {

        }
    }
}