package com.rfz.appflotal.presentation.ui.vialstatus

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.rfz.appflotal.R
import com.rfz.appflotal.presentation.theme.HombreCamionTheme

@Composable
fun VialStatusScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    VialStatusView(
        onBack = onBack
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun VialStatusView(
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {

    var showVialSearch by remember { mutableStateOf(false) }

    VialConfigurationMenu(
        visible = showVialSearch,
        onDismiss = { showVialSearch = false },
        countryFields = emptyList(),
        stateFields = emptyList(),
        onCountryChange = {},
        onStateChange = {}
    ) {
        // onSearch()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Mapa Vial",
                        style = MaterialTheme.typography.titleLarge
                            .copy(
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = Color.White
                ),
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.regresar),
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            showVialSearch = true
                        },
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Icon(
                            Icons.Filled.Search,
                            contentDescription = stringResource(R.string.regresar),
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        if (true) {
            VialStatusPreviewMap(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            )
        } else {
            VialStatusWebView(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            )
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun VialStatusWebView(
    modifier: Modifier = Modifier
) {
    var webView by remember { mutableStateOf<WebView?>(null) }
    var canGoBack by remember { mutableStateOf(false) }
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> webView?.onResume()
                Lifecycle.Event.ON_PAUSE -> webView?.onPause()
                else -> Unit
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            webView?.destroy()
            webView = null
        }
    }

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { context ->
            WebView(context).apply {
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView, url: String) {
                        super.onPageFinished(view, url)

                        view.evaluateJavascript(
                            """
                            (function() {
                                var meta = document.querySelector('meta[name="viewport"]');
                                if (!meta) {
                                    meta = document.createElement('meta');
                                    meta.name = 'viewport';
                                    document.head.appendChild(meta);
                                }
                                meta.content = 'width=700, initial-scale=0.55, minimum-scale=0.1, maximum-scale=5.0';
                            })();
                            """.trimIndent(),
                            null
                        )

                        canGoBack = view.canGoBack()
                    }
                }

                with(settings) {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    loadWithOverviewMode = true
                    useWideViewPort = true
                    builtInZoomControls = true
                    displayZoomControls = false
                    setSupportZoom(true)

                    userAgentString =
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                                "AppleWebKit/537.36 (KHTML, like Gecko) " +
                                "Chrome/120.0.0.0 Safari/537.36"
                }

                loadUrl("https://deldot.gov/map/")
            }.also {
                webView = it
            }
        },
        update = { view ->
            canGoBack = view.canGoBack()
        }
    )

    BackHandler(enabled = canGoBack) {
        webView?.goBack()
    }
}

@Composable
fun VialStatusPreviewMap(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(Color(0xFFEAF4F7))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .background(Color(0xFF285A96)),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    modifier = Modifier.padding(start = 20.dp),
                    text = "Delaware.gov",
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .width(230.dp)
                        .fillMaxHeight()
                        .background(Color(0xFF00508A))
                        .padding(top = 16.dp)
                ) {
                    PreviewMenuItem(text = "MAP LAYERS")
                    PreviewMenuItem(text = "WTMC RADIO")
                    PreviewMenuItem(text = "MY FAVORITE CAMERAS")
                    PreviewMenuItem(text = "MY FAVORITE BUS STOPS")

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        text = "MAP DATA UPDATED",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        text = "53",
                        color = Color.White,
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        text = "SECONDS",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFBDECF4))
                ) {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = "Vista previa del mapa",
                        color = Color(0xFF355C65),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Column(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                    ) {
                        PreviewZoomButton("+")
                        PreviewZoomButton("−")
                    }
                }
            }
        }
    }
}

@Composable
private fun PreviewMenuItem(
    text: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .border(0.5.dp, Color.White.copy(alpha = 0.5f))
            .padding(horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = text,
            color = Color.White,
            fontSize = 15.sp
        )

        Text(
            text = ">",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun PreviewZoomButton(
    text: String
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .background(Color.White)
            .border(1.dp, Color.LightGray),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.DarkGray,
            fontSize = 28.sp
        )
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun VialStatusScreenPreview() {
    HombreCamionTheme {
        VialStatusView(
            modifier = Modifier.fillMaxSize()
        )
    }
}
