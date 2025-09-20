package com.rfz.appflotal.presentation.ui.languaje

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.intl.LocaleList
import androidx.core.os.LocaleListCompat
import com.rfz.appflotal.core.util.AppLocale

@Composable
fun LocalizedApp(content: @Composable () -> Unit) {
    val localeState = AppLocale.currentLocale.collectAsState()
    val context = LocalContext.current

    val localizedContext = remember(localeState.value) {
        val config = Configuration(context.resources.configuration)
        config.setLocale(localeState.value)
        context.createConfigurationContext(config)
    }

    CompositionLocalProvider(
        LocalContext provides localizedContext,
        content = content
    )
}