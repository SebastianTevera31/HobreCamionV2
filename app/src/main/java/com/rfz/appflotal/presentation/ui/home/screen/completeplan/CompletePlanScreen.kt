package com.rfz.appflotal.presentation.ui.home.screen.completeplan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.R
import com.rfz.appflotal.presentation.commons.NoInternetCard
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.components.AlertCard
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.components.BlogPostCard
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.components.EmptyDataCard
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.components.HomeTopBar
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.components.RoadMapPromoCard
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.components.SectionHeader
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.components.SectionsGrid
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.components.SeeAllPill
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.components.VehiclePerformanceCard
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.components.WeatherCard
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.model.CompletePlanUiState
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.model.SectionItem

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
    LazyColumn(
        modifier = modifier
            .fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Spacer(Modifier.height(8.dp))
            HomeTopBar(
                userName = state.userName,
                planType = state.paymentPlanType,
                plates = state.vehiclePlate,
                onNotificationsClick = onNotificationsClick,
                modifier = Modifier.padding(top = 96.dp)
            )
        }

        if (state.isOffline) {
            item {
                NoInternetCard(message = stringResource(R.string.sin_internet_home))
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
                    state.vehicleType,
                    state.vehiclePlate,
                    state.stats,
                    updatedAtLabel = state.performanceDate
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
                    AlertCard(alert, onClick = onAlertsSeeAllClick)
                }
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SectionHeader(stringResource(R.string.clima))
                if (state.weatherState == null) {
                    EmptyDataCard(
                        modifier = Modifier.fillMaxWidth(),
                        message = R.string.sin_datos
                    )
                } else {
                    WeatherCard(
                        state.weatherState.weatherTemp,
                        state.weatherState.weatherCity,
                        stringResource(state.weatherState.weatherDesc),
                        onWeatherClick
                    )
                }
                RoadMapPromoCard(onClick = onMapClick)
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
                if (!state.blogPosts.isEmpty()) {
                    SectionHeader(
                        stringResource(R.string.foro),
                        stringResource(R.string.ver_todas),
                        onBlogSeeAllClick
                    )
                    state.blogPosts.forEach { post ->
                        BlogPostCard(post, onClick = onBlogSeeAllClick)
                    }
                }
            }
        }

        item {
            SeeAllPill(stringResource(R.string.ver_todos), onBlogSeeAllClick)
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
