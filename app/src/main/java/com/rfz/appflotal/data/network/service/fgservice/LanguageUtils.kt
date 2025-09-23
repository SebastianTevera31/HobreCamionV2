package com.rfz.appflotal.data.network.service.fgservice

import android.content.Context
import android.content.res.Configuration
import com.rfz.appflotal.core.util.AppLocale
import java.util.Locale

fun Context.localized(appLocale: Locale): Context {
    val conf = Configuration(resources.configuration)
    conf.setLocales(android.os.LocaleList(appLocale))
    return createConfigurationContext(conf)
}

fun currentAppLocaleFromAppCompat(): Locale? {
    val language = AppLocale.currentLocale.value.language
    return if (language.isNotEmpty()) Locale(language) else null
}