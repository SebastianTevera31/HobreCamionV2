package com.rfz.appflotal.presentation.ui.password.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PasswordViewModel @Inject constructor() : ViewModel() {
    private val _password = MutableLiveData<String>()
    val password: LiveData<String> = _password

    private var _repeatpassword = MutableLiveData<String>()
    val repeatpassword: LiveData<String> = _repeatpassword

    private var _isenablePassword = MutableLiveData<Boolean>()
    val isenablePassword: LiveData<Boolean> = _isenablePassword

    fun onPasswordChanged(password: String, repeatpassword: String) {
        _password.value = password
        _repeatpassword.value = repeatpassword
        _isenablePassword.value = isPasswordEnabled(password, repeatpassword)
    }

    fun isPasswordEnabled(password: String, repeatpassword: String): Boolean {
        return password.isNotEmpty() && repeatpassword.isNotEmpty()
    }

}