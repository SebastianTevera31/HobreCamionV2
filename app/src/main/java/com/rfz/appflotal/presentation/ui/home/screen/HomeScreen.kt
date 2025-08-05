package com.rfz.appflotal.presentation.ui.home.screen

import android.content.res.Configuration
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.rfz.appflotal.R
import com.rfz.appflotal.core.network.NetworkConfig
import com.rfz.appflotal.core.util.HombreCamionScreens
import com.rfz.appflotal.presentation.ui.home.viewmodel.HomeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel,
    colors: ColorScheme
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val resources = context.resources

    val uiState by homeViewModel.uiState.collectAsState()
    val message by homeViewModel.homeCheckInMessage.observeAsState()

    LaunchedEffect(Unit) {
        homeViewModel.loadInitialData()
    }

    val onlyLanguagesAllowedText = stringResource(R.string.only_languages_allowed)
    val languages = listOf("es" to "ES", "en" to "EN")
    val userName = uiState.userData?.fld_name ?: stringResource(R.string.operator)

    LaunchedEffect(uiState.selectedLanguage) {
        if (uiState.selectedLanguage == "es" || uiState.selectedLanguage == "en") {
            val result = homeViewModel.changeLanguage(uiState.selectedLanguage)
            if (result.isSuccess && result.getOrNull()?.mensaje == "Lenguaje cambiado correctamente.") {
                val locale = Locale(uiState.selectedLanguage)
                Locale.setDefault(locale)
                val config = Configuration(configuration)
                config.setLocale(locale)
                context.createConfigurationContext(config)
                resources.updateConfiguration(config, resources.displayMetrics)
            }
        } else {
            Toast.makeText(
                context,
                onlyLanguagesAllowedText,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    val menuItems = listOf(
        MenuItem(
            stringResource(R.string.brands),
            HombreCamionScreens.MARCAS.name,
            R.drawable.ic_brand
        ),
        MenuItem(
            stringResource(R.string.original_design),
            HombreCamionScreens.DISENIO_ORIGINAL.name,
            R.drawable.ic_tire_design
        ),
        MenuItem(
            stringResource(R.string.tire_sizes),
            HombreCamionScreens.DIMENSIONES.name,
            R.drawable.ic_tire_size
        ),
        MenuItem(
            stringResource(R.string.products),
            HombreCamionScreens.PRODUCTOS.name,
            R.drawable.ic_products
        ),
        MenuItem(
            stringResource(R.string.tire_register),
            HombreCamionScreens.LLANTAS.name,
            R.drawable.ic_tire_register
        ),
        MenuItem(
            stringResource(R.string.vehicle_register),
            HombreCamionScreens.VEHICULOS.name,
            R.drawable.ic_truck
        ),
        MenuItem(
            stringResource(R.string.tire_change),
            HombreCamionScreens.MONTAJE.name,
            R.drawable.ic_tire_change
        ),
        MenuItem(
            title = "Monitoreo",
            route = HombreCamionScreens.MONITOR.name,
            iconRes = R.drawable.monitor
        )
    )

    val scope = rememberCoroutineScope()

    val primaryColor = Color(0xFF4A3DAD)
    val primaryLight = Color(0xFF6A5DD9)
    val secondaryColor = Color(0xFF5C4EC9)
    val accentColor = Color(0xFF7D6BFF)
    val lightBackground = Color(0xFFF9F8FF)
    val cardBackground = Color.White
    val surfaceColor = Color(0xFFF1EFFF)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.app_name),
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = primaryColor,
                    titleContentColor = Color.White
                ),
                actions = {
                    Box(
                        modifier = Modifier
                            .width(80.dp)
                            .height(40.dp)
                            .padding(end = 8.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White.copy(alpha = 0.2f))
                            .border(
                                width = 1.dp,
                                color = Color.White.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(20.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        ) {
                            languages.forEach { (code, display) ->
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .clickable {
                                            scope.launch {
                                                homeViewModel.changeLanguage(code)
                                            }
                                        }
                                        .background(
                                            if (uiState.selectedLanguage == code)
                                                Color.White.copy(alpha = 0.3f)
                                            else
                                                Color.Transparent
                                        )
                                        .padding(6.dp)
                                ) {
                                    Text(
                                        text = display,
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = if (uiState.selectedLanguage == code) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                                if (code != languages.last().first) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                }
                            }
                        }
                    }

                    IconButton(
                        onClick = {
                            CoroutineScope(Dispatchers.IO).launch {
                                homeViewModel.logout()
                                withContext(Dispatchers.Main) {
                                    navController.navigate(NetworkConfig.LOGIN) {
                                        popUpTo(0)
                                    }
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ExitToApp,
                            contentDescription = stringResource(R.string.logout),
                            tint = Color.White
                        )
                    }

                    IconButton(
                        onClick = { /* Acción perfil */ }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AccountCircle,
                            contentDescription = stringResource(R.string.profile),
                            tint = Color.White
                        )
                    }
                }
            )
        },
        containerColor = lightBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(primaryColor, primaryLight),
                            startY = 0f,
                            endY = 500f
                        ),
                        shape = RoundedCornerShape(
                            bottomStart = 77.dp,
                            bottomEnd = 77.dp
                        )
                    )
                    .shadow(
                        elevation = 12.dp,
                        shape = RoundedCornerShape(
                            bottomStart = 48.dp,
                            bottomEnd = 48.dp
                        ),
                        spotColor = primaryColor.copy(alpha = 0.3f)
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = stringResource(R.string.logo_description),
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .height(80.dp)
                            .padding(bottom = 8.dp)
                    )

                    Text(
                        text = stringResource(R.string.welcome, userName),
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )

                    Text(
                        text = LocalDate.now().toString(),
                        color = Color.White.copy(alpha = 0.9f),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .background(surfaceColor)
                    .clip(
                        RoundedCornerShape(
                            topStart = 32.dp,
                            topEnd = 32.dp
                        )
                    )
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(menuItems) { item ->
                        ElegantMenuCard(
                            title = item.title,
                            iconRes = item.iconRes,
                            onClick = { navController.navigate(item.route) },
                            primaryColor = primaryColor,
                            secondaryColor = secondaryColor,
                            cardBackground = cardBackground
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                primaryColor.copy(alpha = 0.9f),
                                secondaryColor.copy(alpha = 0.9f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.copyright, LocalDate.now().year),
                    color = Color.White.copy(alpha = 0.95f),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

@Composable
private fun ElegantMenuCard(
    title: String,
    iconRes: Int,
    onClick: () -> Unit,
    primaryColor: Color = Color(0xFF5B4B8A),
    secondaryColor: Color = Color(0xFF9B87FF),
    cardBackground: Color = Color.White
) {
    val highlightColor = Color(0xFFEDE7FF)
    val accentColor = Color(0xFFD1C4FF)

    Card(
        modifier = Modifier
            .height(180.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = primaryColor.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardBackground)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            cardBackground,
                            highlightColor.copy(alpha = 0.1f)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    secondaryColor.copy(alpha = 0.2f),
                                    primaryColor.copy(alpha = 0.1f)
                                ),
                                center = Offset(0.3f, 0.3f),
                                radius = 100f
                            )
                        )
                        .padding(18.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = iconRes),
                        contentDescription = title,
                        modifier = Modifier
                            .size(40.dp),
                        colorFilter = ColorFilter.tint(primaryColor)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = primaryColor
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }
    }
}

private data class MenuItem(
    val title: String,
    val route: String,
    val iconRes: Int
)