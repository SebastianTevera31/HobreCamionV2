package com.rfz.appflotal.core.util

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

object AppLocale {
    private val _currentLocale = MutableStateFlow(Locale("en"))
    val currentLocale: StateFlow<Locale> = _currentLocale.asStateFlow()

    fun setLocale(locale: Locale) {
        _currentLocale.value = locale
        Locale.setDefault(locale)
    }

    fun loadSavedLocale(context: Context) {
        val prefs = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val lang = prefs.getString("app_language", "en") ?: "en"
        _currentLocale.value = Locale(lang)
    }
}