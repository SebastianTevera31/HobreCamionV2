package com.rfz.appflotal.presentation.ui.home.screen.completeplan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rfz.appflotal.presentation.ui.inicio.ui.PaymentPlanType
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.model.AlertStatus
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.model.AlertUi
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.model.BlogPost
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.model.CompletePlanUiState
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.model.SectionItem
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.model.VehicleStat
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.utils.CompletePlanColors.CriticalBg
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.utils.CompletePlanColors.CriticalFg
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.utils.CompletePlanColors.PendingBg
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.utils.CompletePlanColors.PendingFg
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.utils.CompletePlanColors.SubtleText
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.utils.CompletePlanColors.TealDark
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.utils.CompletePlanColors.TealMid
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.utils.CompletePlanColors.TealSoftBg
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.utils.CompletePlanColors.WeatherBg
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.utils.bottomNavItems

@Composable
fun CompletePlanScreen(
    state: CompletePlanUiState = CompletePlanUiState(),
    onNotificationsClick: () -> Unit = {},
    onVehicleDetailClick: () -> Unit = {},
    onAlertsSeeAllClick: () -> Unit = {},
    onWeatherMapClick: () -> Unit = {},
    onSectionClick: (SectionItem) -> Unit = {},
    onBlogSeeAllClick: () -> Unit = {},
    onNavItemClick: (Int) -> Unit = {}
) {
    Scaffold(
        containerColor = Color.White,
        bottomBar = { HomeBottomBar(selected = 0, onItemClick = onNavItemClick) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                HomeTopBar(
                    userName = state.userName,
                    planType = state.paymentPlanType,
                    plates = state.vehiclePlate,
                    onNotificationsClick = onNotificationsClick
                )
            }

            item {
                SectionHeader("Rendimiento del vehículo", "Ver detalle", onVehicleDetailClick)
            }
            item {
                VehiclePerformanceCard(
                    state.vehicleName,
                    state.vehiclePlate,
                    state.periodLabel,
                    state.stats
                )
            }

            item {
                SectionHeader("Alertas recientes", "Ver todas", onAlertsSeeAllClick)
            }
            items(state.alerts) { alert -> AlertCard(alert) }

            item {
                SectionHeader("Clima", "Ver mapa", onWeatherMapClick)
            }
            item {
                WeatherCard(
                    state.weatherTemp,
                    state.weatherCity,
                    state.weatherDesc,
                    onWeatherMapClick
                )
            }

            item {
                Text(
                    "Secciones",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            item {
                SectionsGrid(state.sections, onSectionClick)
            }

            item {
                SectionHeader("Blog", "Ver todas", onBlogSeeAllClick)
            }
            items(state.blogPosts) { post -> BlogPostCard(post) }
            item {
                SeeAllPill("Ver todos", onBlogSeeAllClick)
            }
        }
    }
}

@Composable
private fun HomeTopBar(
    userName: String,
    planType: PaymentPlanType,
    plates: String,
    onNotificationsClick: () -> Unit
) {
    val isComplete = planType == PaymentPlanType.Complete
    val horizontalArrangement = if (isComplete) Arrangement.Center else Arrangement.SpaceBetween
    val textAlignment = if (isComplete) Alignment.CenterHorizontally else Alignment.Start

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = horizontalArrangement
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = if (isComplete) Modifier.weight(1f) else Modifier
        ) {
            if (!isComplete) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(TealSoftBg),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        userName.take(2).uppercase(),
                        color = TealMid,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.width(12.dp))
            }

            Column(
                modifier = if (isComplete) Modifier.fillMaxWidth() else Modifier,
                horizontalAlignment = textAlignment
            ) {
                Text(
                    if (isComplete) "Bienvenido, $userName" else "¡Hola, $userName!",
                    style = if (isComplete) MaterialTheme.typography.titleLarge else MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = if (isComplete) TextAlign.Center else TextAlign.Start
                )
                Text(
                    "Plan: ${planType.name}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TealMid,
                    fontWeight = FontWeight.SemiBold
                )
                if (plates.isNotEmpty()) {
                    Text(
                        "Placas: $plates",
                        style = MaterialTheme.typography.bodySmall,
                        color = SubtleText,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        if (!isComplete) {
            Box {
                IconButton(
                    onClick = onNotificationsClick,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(TealSoftBg)
                ) {
                    Icon(
                        Icons.Outlined.Notifications,
                        contentDescription = "Notificaciones",
                        tint = TealDark
                    )
                }
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .align(Alignment.TopEnd)
                        .clip(CircleShape)
                        .background(CriticalFg)
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String, actionLabel: String, onActionClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(
            actionLabel,
            color = TealMid,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(4.dp)
        )
    }
}

@Composable
private fun VehiclePerformanceCard(
    vehicleName: String,
    plate: String,
    periodLabel: String,
    stats: List<VehicleStat>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = TealDark)
    ) {
        Column(Modifier.padding(20.dp)) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "$vehicleName · $plate",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodyLarge
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Color.White.copy(alpha = 0.18f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        periodLabel,
                        color = Color.White,
                        fontSize = MaterialTheme.typography.labelSmall.fontSize,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
            Spacer(Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                stats.forEach { stat -> VehicleStatItem(stat) }
            }
        }
    }
}

@Composable
private fun VehicleStatItem(stat: VehicleStat) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(stat.icon, contentDescription = null, tint = Color.White.copy(alpha = 0.85f))
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            Text(stat.value, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text(" ${stat.unit}", color = Color.White.copy(alpha = 0.85f), fontSize = 13.sp)
        }
        Text(stat.label, color = Color.White.copy(alpha = 0.85f), fontSize = 12.sp)
    }
}

@Composable
private fun AlertCard(alert: AlertUi) {
    val (bg, fg, label) = when (alert.status) {
        AlertStatus.CRITICA -> Triple(CriticalBg, CriticalFg, "CRÍTICA")
        AlertStatus.PENDIENTE -> Triple(PendingBg, PendingFg, "PENDIENTE")
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(alert.icon, contentDescription = null, tint = TealMid)
                    Spacer(Modifier.width(10.dp))
                    Text(alert.title, fontWeight = FontWeight.SemiBold)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(bg)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(label, color = fg, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Outlined.Error,
                    contentDescription = null,
                    tint = SubtleText,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    alert.detailLabel,
                    color = SubtleText,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    alert.detailValue,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodySmall
                )
                alert.detailExtra?.let {
                    Spacer(Modifier.width(4.dp))
                    Text(it, color = SubtleText, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
private fun WeatherCard(temp: String, city: String, description: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = WeatherBg)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Outlined.WbSunny,
                contentDescription = null,
                tint = TealDark,
                modifier = Modifier.size(40.dp)
            )
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(temp, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                Text(city, fontWeight = FontWeight.SemiBold)
                Text(description, color = SubtleText, style = MaterialTheme.typography.bodySmall)
            }
            Icon(Icons.Outlined.ChevronRight, contentDescription = "Ver mapa", tint = TealDark)
        }
    }
}

@Composable
private fun SectionsGrid(sections: List<SectionItem>, onSectionClick: (SectionItem) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = Modifier.height(if (sections.size > 4) 190.dp else 95.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(sections) { section -> SectionIconItem(section, onSectionClick) }
    }
}

@Composable
private fun SectionIconItem(section: SectionItem, onClick: (SectionItem) -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(TealSoftBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(section.icon, contentDescription = section.label, tint = TealDark)
        }
        Spacer(Modifier.height(6.dp))
        Text(
            section.label,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            maxLines = 2
        )
    }
}

@Composable
private fun BlogPostCard(post: BlogPost) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(TealSoftBg)
            )
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    post.category,
                    color = TealMid,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    post.title,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    post.excerpt,
                    color = SubtleText,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2
                )
            }
        }
    }
}

@Composable
private fun SeeAllPill(label: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(50)),
        color = TealSoftBg,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(vertical = 14.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, color = TealDark, fontWeight = FontWeight.Bold)
            Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = TealDark)
        }
    }
}

@Composable
private fun HomeBottomBar(selected: Int, onItemClick: (Int) -> Unit) {
    Surface(
        color = TealDark,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            bottomNavItems.forEachIndexed { index, item ->
                val isSelected = index == selected
                Box {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(if (isSelected) Color.White.copy(alpha = 0.15f) else Color.Transparent)
                            .padding(
                                horizontal = if (isSelected) 14.dp else 10.dp,
                                vertical = 10.dp
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(item.icon, contentDescription = item.label, tint = Color.White)
                        if (isSelected) {
                            Spacer(Modifier.width(6.dp))
                            Text(
                                item.label,
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                        }
                    }
                    if (item.hasBadge) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .align(Alignment.TopEnd)
                                .clip(CircleShape)
                                .background(Color(0xFFFFC107))
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun CompletePlanScreenPreview() {
    CompletePlanScreen()
}
