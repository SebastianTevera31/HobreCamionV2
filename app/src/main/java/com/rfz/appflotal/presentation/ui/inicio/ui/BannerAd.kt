package com.rfz.appflotal.presentation.ui.inicio.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdView

@Composable
fun BannerAd(adUnitId: String, onAdView: () -> AdView) {
    AndroidView(
        factory = { context ->
            onAdView()
        },
        modifier = Modifier.fillMaxWidth()
    )
}