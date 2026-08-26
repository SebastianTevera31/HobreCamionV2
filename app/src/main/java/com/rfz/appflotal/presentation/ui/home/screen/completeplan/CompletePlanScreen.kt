package com.rfz.appflotal.presentation.ui.home.screen.completeplan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.R
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.components.AlertCard
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.components.BlogPostCard
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.components.CompleteHomeTopBar
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.components.HomeTopBar
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.components.SectionHeader
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.components.SectionsGrid
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.components.SeeAllPill
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.components.VehiclePerformanceCard
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.components.WeatherCard
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.model.CompletePlanUiState
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.model.SectionItem
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
    modifier: Modifier = Modifier,
    state: CompletePlanUiState = CompletePlanUiState()
) {
    Scaffold(
        containerColor = Color.White,
        modifier = modifier,
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                if (state.paymentPlanType == PaymentPlanType.Complete) {
                    CompleteHomeTopBar(
                        state.userName,
                        state.paymentPlanType,
                        onNotificationsClick
                    )
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
                        stringResource(R.string.rendimiento_del_vehiculo),
                        stringResource(R.string.ver_mas),
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
                    SectionHeader(
                        stringResource(R.string.alertas_recientes),
                        stringResource(R.string.ver_todas), onAlertsSeeAllClick
                    )
                    state.alerts.forEach { alert ->
                        AlertCard(alert)
                    }
                }
            }

            item {
                Column {
                    SectionHeader(
                        stringResource(R.string.clima),
                        stringResource(R.string.ver_mapa), onMapClick
                    )
                    WeatherCard(
                        state.weatherTemp,
                        state.weatherCity,
                        stringResource(state.weatherDesc),
                        onWeatherClick
                    )
                }
            }

            item {
                Column {
                    Text(
                        stringResource(R.string.secciones),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.padding(8.dp))
                    SectionsGrid(state.sections, onSectionClick)
                }
            }

            item {
                Column {
                    SectionHeader(
                        stringResource(R.string.foro),
                        stringResource(R.string.ver_todas),
                        onBlogSeeAllClick
                    )
                    state.blogPosts.forEach { post ->
                        BlogPostCard(post)
                    }
                }
            }

            item {
                SeeAllPill(stringResource(R.string.ver_todos), onBlogSeeAllClick)
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
        onMapClick = {},
        onWeatherClick = {}
    )
}
