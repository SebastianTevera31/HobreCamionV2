package com.rfz.appflotal.core.util

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

object AppLocale {

    private val _currentLocale = MutableStateFlow(getInitialLocale())
    val currentLocale: StateFlow<Locale> = _currentLocale.asStateFlow()
    fun setLocale(locale: Locale) {
        _currentLocale.value = locale
        Locale.setDefault(locale)
    }

    /** Ingles siempre como en_US (formatos de fecha/numero de EUA); cualquier otro idioma tal cual. */
    fun forLanguage(language: String): Locale =
        if (language.startsWith("en")) Locale.US else Locale(language)

    fun loadSavedLocale(context: Context) {
        val prefs = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val lang = prefs.getString("app_language", Locale.getDefault().language)
            ?: Locale.getDefault().language
        _currentLocale.value = forLanguage(lang)
    }

    private fun getInitialLocale(): Locale {
        val systemLang = Locale.getDefault().language
        return if (systemLang.contains("es")) Locale("es")
        else Locale.US
    }


    fun getSystemLocale(): Locale {
        val lang = _currentLocale.value.language
        return if (lang.contains("es")) Locale("es")
        else Locale.US
    }
}
