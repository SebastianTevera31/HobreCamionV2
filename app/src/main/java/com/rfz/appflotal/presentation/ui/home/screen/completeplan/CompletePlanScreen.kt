package com.rfz.appflotal.presentation.ui.home.screen.completeplan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.components.AlertCard
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.components.BlogPostCard
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.components.CompleteHomeTopBar
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.components.HomeBottomBar
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.components.HomeTopBar
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.components.SectionHeader
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.components.SectionsGrid
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.components.SeeAllPill
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.components.VehiclePerformanceCard
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.components.WeatherCard
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.model.CompletePlanUiState
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.model.SectionItem
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.utils.BottomNavItems
import com.rfz.appflotal.presentation.ui.inicio.ui.PaymentPlanType

@Composable
fun CompletePlanScreen(
    onNotificationsClick: () -> Unit,
    onVehicleDetailClick: () -> Unit,
    onAlertsSeeAllClick: () -> Unit,
    onMapClick: () -> Unit,
    onWeatherClick: () -> Unit,
    onSectionClick: (SectionItem) -> Unit,
    onBlogSeeAllClick: () -> Unit,
    onNavItemClick: (BottomNavItems) -> Unit,
    modifier: Modifier = Modifier,
    state: CompletePlanUiState = CompletePlanUiState()
) {
    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            HomeBottomBar(selected = state.currentScreen, onItemClick = onNavItemClick)
        },
        modifier = modifier.navigationBarsPadding(),
    ) { padding ->
        when (state.currentScreen) {
            BottomNavItems.HOME -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(padding),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    item {
                        if (state.paymentPlanType == PaymentPlanType.Complete) {
                            CompleteHomeTopBar(state.userName, onNotificationsClick)
                        } else {
                            HomeTopBar(
                                userName = state.userName,
                                planType = state.paymentPlanType,
                                plates = state.vehiclePlate,
                                onNotificationsClick = onNotificationsClick,
                            )
                        }
                    }

                    item {
                        Column {
                            SectionHeader(
                                "Rendimiento del vehículo",
                                "Ver detalle",
                                onVehicleDetailClick
                            )
                            VehiclePerformanceCard(
                                state.vehicleName,
                                state.vehiclePlate,
                                state.stats
                            )
                        }
                    }

                    item {
                        Column {
                            SectionHeader("Alertas recientes", "Ver todas", onAlertsSeeAllClick)
                            state.alerts.forEach { alert ->
                                AlertCard(alert)
                            }
                        }
                    }

                    item {
                        Column {
                            SectionHeader("Clima", "Ver mapa", onMapClick)
                            WeatherCard(
                                state.weatherTemp,
                                state.weatherCity,
                                state.weatherDesc,
                                onWeatherClick
                            )
                        }
                    }

                    item {
                        Column {
                            Text(
                                "Secciones",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.padding(8.dp))
                            SectionsGrid(state.sections, onSectionClick)
                        }
                    }

                    item {
                        Column {
                            SectionHeader("Blog", "Ver todas", onBlogSeeAllClick)
                            state.blogPosts.forEach { post ->
                                BlogPostCard(post)
                            }
                        }
                    }

                    item {
                        SeeAllPill("Ver todos", onBlogSeeAllClick)
                    }
                }
            }

            BottomNavItems.ANALYTICS -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Pantalla de Analytics (Contenido persistente)",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }

            BottomNavItems.MAP -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Pantalla de Mapa Vial (Contenido persistente)",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }

            BottomNavItems.FORUM -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Pantalla de Foro (Contenido persistente)",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }

            BottomNavItems.MONITOR -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Pantalla de Monitor (Contenido persistente)",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun CompletePlanScreenPreview() {
    CompletePlanScreen(
        onNotificationsClick = {},
        onVehicleDetailClick = {},
        onAlertsSeeAllClick = {},
        onSectionClick = {},
        onBlogSeeAllClick = {},
        onNavItemClick = {},
        onMapClick = {},
        onWeatherClick = {}
    )
}
