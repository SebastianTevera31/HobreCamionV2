package com.rfz.appflotal.presentation.ui.inicio.ui

import android.view.ViewGroup
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
    var adView: AdView? = remember { null }
    AndroidView(
        modifier = modifier,
        factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                this.adUnitId = adUnitId
                loadAd(AdRequest.Builder().build())

            }.also {
                adView = it
            }
        }
    )

    DisposableEffect(Unit) {
        onDispose {
            // liberar cuando el Composable sale de composición
            adView?.let { view ->
                (view.parent as? ViewGroup)?.removeView(view)
                view.destroy()
            }
        }
    }
}