package com.rfz.proyectogasmonsoft.presentation.ui.login.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rfz.proyectogasmonsoft.domain.login.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel() {


    private val _usuario = MutableLiveData<String>()
    val usuario: LiveData<String> = _usuario

    private val _password = MutableLiveData<String>()
    val password: LiveData<String> = _password

    private val _isLoginEnable = MutableLiveData<Boolean>()
    val isLoginEnable: LiveData<Boolean> = _isLoginEnable

    fun onLoginChanged(usuario: String, password: String) {

        _usuario.value = usuario
        _password.value = password
        _isLoginEnable.value = enableLogin(usuario, password)

    }

    fun enableLogin(usuario: String, password: String): Boolean {
        return usuario.isNotEmpty() && password.isNotEmpty()

    }

}