package com.rfz.appflotal.presentation.ui.inicio.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
fun GlobalAdMobBanner(
    adUnitId: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val adView = remember {
        AdView(context).apply {
            AdSize.BANNER
            this.adUnitId = adUnitId
            loadAd(AdRequest.Builder().build())
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { adView }
    )

    DisposableEffect(Unit) {
        onDispose {
            // SOLO cuando la app se cierra
            adView.destroy()
        }
    }
}