package com.rfz.appflotal.presentation.ui.inicio.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfz.appflotal.data.model.flotalSoft.AppHCEntity
import com.rfz.appflotal.domain.database.DeleteTasksUseCase
import com.rfz.appflotal.domain.database.GetTasksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InicioScreenViewModel @Inject constructor(
    private val getTasksUseCase: GetTasksUseCase,
    private val deleteTasksUseCase: DeleteTasksUseCase
) : ViewModel() {

    private val _initialValidationCompleted = MutableLiveData<Boolean>(false)
    val initialValidationCompleted: LiveData<Boolean> = _initialValidationCompleted

    private val _userData = MutableLiveData<AppHCEntity?>()
    val userData: LiveData<AppHCEntity?> = _userData

    init {
        checkUserSession()
    }

    private fun checkUserSession() {
        viewModelScope.launch {
            try {
                val tasks = getTasksUseCase().first()
                _userData.value = tasks.firstOrNull()
                _initialValidationCompleted.value = true
            } catch (e: Exception) {
                _initialValidationCompleted.value = true
            }
        }
    }

    fun deleteUserData() {
        viewModelScope.launch {
            try {
                deleteTasksUseCase()
                _userData.value = null
            } catch (e: Exception) {

            }
        }
    }
}