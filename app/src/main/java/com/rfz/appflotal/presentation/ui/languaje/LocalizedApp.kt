package com.rfz.appflotal.presentation.ui.languaje

import android.content.ContextWrapper
import android.content.res.AssetManager
import android.content.res.Configuration
import android.content.res.Resources
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.rfz.appflotal.core.util.AppLocale

@Composable
fun LocalizedApp(content: @Composable () -> Unit) {
    val localeState by AppLocale.currentLocale.collectAsState()
    val context = LocalContext.current

    val localizedContext = remember(localeState, context) {
        val config = Configuration(context.resources.configuration)
        config.setLocale(localeState)
        val configurationContext = context.createConfigurationContext(config)

        object : ContextWrapper(context) {
            override fun getResources(): Resources = configurationContext.resources
            override fun getAssets(): AssetManager = configurationContext.assets
            override fun getSystemService(name: String): Any? =
                configurationContext.getSystemService(name)
        }
    }

    CompositionLocalProvider(
        LocalContext provides localizedContext,
        content = content
    )
}