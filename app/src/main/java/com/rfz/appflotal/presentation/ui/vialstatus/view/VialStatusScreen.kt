package com.rfz.appflotal.presentation.ui.vialstatus.view

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.rfz.appflotal.R
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.components.LoadingDialog
import com.rfz.appflotal.presentation.ui.utils.LoadState
import com.rfz.appflotal.presentation.ui.vialstatus.viewmodel.VialStatusViewModel
import com.rfz.appflotal.presentation.ui.vialstatus.viewmodel.VialUiStatus

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun VialStatusScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: VialStatusViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getCurrentLocation()
    }

    VialStatusView(
        modifier = modifier,
        uiState = uiState,
        onBack = onBack,
        onCountryChange = viewModel::changeCountry,
        onStateChange = viewModel::changeState,
        onSearch = viewModel::getMap,
        onReduceScale = viewModel::reduceScale,
        onIncreaseScale = viewModel::increaseScale
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun VialStatusView(
    uiState: VialUiStatus,
    onBack: () -> Unit,
    onCountryChange: (Int) -> Unit,
    onStateChange: (Int) -> Unit,
    onSearch: () -> Unit,
    onReduceScale: () -> Unit,
    onIncreaseScale: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showVialSearch by rememberSaveable { mutableStateOf(false) }

    val title = uiState.selectedState?.let { selectedState ->
        stringResource(R.string.mapa_vial_con_estado, selectedState.description)
    } ?: stringResource(R.string.vial_status)

    val isLoading =
        uiState.gettingMapStatus is LoadState.Loading ||
                uiState.gettingStatesStatus is LoadState.Loading

    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.padding(start = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.regresar),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            showVialSearch = true
                        },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.onPrimary.copy(
                                alpha = 0.14f
                            ),
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = stringResource(R.string.buscar),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            if (uiState.mapUrl.isNotBlank()) {
                VialStatusWebView(
                    url = uiState.mapUrl,
                    initScale = uiState.initScale,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                EmptyMapState(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            if (isLoading) {
                LoadingDialog()
            }

            if (uiState.mapUrl.isNotBlank()) {
                Column(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.BottomEnd)
                ) {
                    IconButton(
                        onClick = onIncreaseScale,
                        colors = IconButtonDefaults.iconButtonColors(MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    IconButton(
                        onClick = onReduceScale,
                        colors = IconButtonDefaults.iconButtonColors(MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }

            VialLocationMenu(
                visible = showVialSearch,
                onDismiss = {
                    showVialSearch = false
                },
                countryFields = uiState.countries,
                stateFields = uiState.states,
                selectedCountry = uiState.selectedCountry,
                selectedState = uiState.selectedState,
                onCountryChange = onCountryChange,
                onStateChange = onStateChange,
                onSearch = {
                    onSearch()
                    showVialSearch = false
                }
            )
        }
    }
}

@Composable
private fun EmptyMapState(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Map,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(48.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(R.string.selecciona_una_ubicacion_para_consultar_el_mapa_vial),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun VialStatusWebView(
    url: String,
    initScale: Double,
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
                                meta.content = 'width=700, initial-scale=0.5, minimum-scale=0.1, maximum-scale=5.0';
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

                loadUrl(url)
            }.also {
                webView = it
            }
        },
        update = { view ->
            if (view.url != url) {
                view.loadUrl(url)
            }
            
            // Actualizar la escala sin recargar la página completa
            view.evaluateJavascript(
                """
                (function() {
                    var meta = document.querySelector('meta[name="viewport"]');
                    if (meta) {
                        meta.content = 'width=700, initial-scale=${initScale}, minimum-scale=0.1, maximum-scale=5.0';
                    }
                })();
                """.trimIndent(),
                null
            )

            canGoBack = view.canGoBack()
        }
    )

    BackHandler(enabled = canGoBack) {
        webView?.goBack()
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
            uiState = VialUiStatus(),
            onBack = {},
            onCountryChange = {},
            onStateChange = {},
            onSearch = {},
            onReduceScale = {},
            onIncreaseScale = {}
        )
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun VialStatusWithMenuPreview() {
    HombreCamionTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            VialStatusView(
                uiState = VialUiStatus(),
                onBack = {},
                onCountryChange = {},
                onStateChange = {},
                onSearch = {},
                onReduceScale = {},
                onIncreaseScale = {}
            )
            // Force the menu to be visible for validation
            VialLocationMenu(
                visible = true,
                onDismiss = {},
                countryFields = emptyList(),
                stateFields = emptyList(),
                selectedCountry = null,
                selectedState = null,
                onCountryChange = {},
                onStateChange = {},
                onSearch = {}
            )
        }
    }
}
