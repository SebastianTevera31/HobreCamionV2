package com.rfz.appflotal.presentation.ui.home.screen.completeplan.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.outlined.GpsFixed
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.QueryStats
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rfz.appflotal.R
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.model.AlertStatus
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.model.AlertUi
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.model.BlogPost
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.model.IconResource
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.model.SectionItem
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.model.VehicleStat
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.model.asIcon
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.utils.BottomNavItems
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.utils.CompletePlanColors.CriticalBg
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.utils.CompletePlanColors.CriticalFg
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.utils.CompletePlanColors.SubtleText
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.utils.CompletePlanColors.TealDark
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.utils.CompletePlanColors.TealMid
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.utils.CompletePlanColors.TealSoftBg
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.utils.CompletePlanColors.WeatherBg
import com.rfz.appflotal.presentation.ui.inicio.ui.PaymentPlanType

@Composable
fun AdaptiveIcon(
    icon: IconResource,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    when (icon) {
        is IconResource.Vector -> Icon(
            imageVector = icon.imageVector,
            contentDescription = contentDescription,
            modifier = modifier.size(24.dp),
            tint = tint
        )

        is IconResource.Drawable -> Icon(
            painter = painterResource(id = icon.resId),
            contentDescription = contentDescription,
            modifier = modifier.size(24.dp),
            tint = tint
        )
    }
}

@Composable
fun CompleteHomeTopBar(
    userName: String,
    onNotificationsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 68.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
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
            Column {
                Text(
                    "¡Hola, $userName!",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Revisa tu camión",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SubtleText
                )
            }
        }

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

@Composable
fun HomeTopBar(
    userName: String,
    planType: PaymentPlanType,
    plates: String,
    onNotificationsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
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

            Column(horizontalAlignment = Alignment.Start) {
                Text(
                    text = "¡Hola, $userName!",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start
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

@Composable
fun SectionHeader(title: String, actionLabel: String, onActionClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        TextButton(onClick = onActionClick) {
            Text(
                actionLabel,
                color = TealMid,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}

@Composable
fun VehiclePerformanceCard(
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
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
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
fun VehicleStatItem(stat: VehicleStat) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        AdaptiveIcon(stat.icon, contentDescription = null, tint = Color.White.copy(alpha = 0.85f))
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            Text(stat.value, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text(" ${stat.unit}", color = Color.White.copy(alpha = 0.85f), fontSize = 13.sp)
        }
        Text(stat.label, color = Color.White.copy(alpha = 0.85f), fontSize = 12.sp)
    }
}

@Composable
fun AlertCard(alert: AlertUi) {
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(3f)
                ) {
                    AdaptiveIcon(alert.icon, contentDescription = null, tint = TealMid)

                    Spacer(Modifier.width(10.dp))
                    Text(
                        alert.title,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                if (alert.status == AlertStatus.CRITICA) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(CriticalBg)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "CRÍTICA",
                            color = CriticalFg,
                            fontSize = MaterialTheme.typography.labelSmall.fontSize,
                            fontWeight = FontWeight.Bold
                        )
                    }
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
fun WeatherCard(temp: String, city: String, description: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = WeatherBg),
        onClick = onClick
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
fun SectionsGrid(sections: List<SectionItem>, onSectionClick: (SectionItem) -> Unit) {
    val rows = sections.chunked(4)
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        rows.forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowItems.forEach { section ->
                    Box(modifier = Modifier.weight(1f)) {
                        SectionIconItem(section, onSectionClick)
                    }
                }
                // Completar el espacio si la fila tiene menos de 4 elementos
                if (rowItems.size < 4) {
                    repeat(4 - rowItems.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun SectionIconItem(section: SectionItem, onClick: (SectionItem) -> Unit) {
    Card(
        onClick = { onClick(section) },
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
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
                AdaptiveIcon(
                    section.icon,
                    contentDescription = stringResource(section.label),
                    tint = TealDark
                )
            }
            Spacer(Modifier.height(6.dp))
            Text(
                stringResource(section.label),
                fontSize = MaterialTheme.typography.labelSmall.fontSize,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}

@Composable
fun BlogPostCard(post: BlogPost) {
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
fun SeeAllPill(label: String, onClick: () -> Unit) {
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
fun HomeBottomBar(selected: BottomNavItems, onItemClick: (BottomNavItems) -> Unit) {
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
            BottomNavItems.entries.forEach { item ->
                val isSelected = item == selected
                Box(modifier = Modifier.clickable { onItemClick(item) }) {
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
                        AdaptiveIcon(
                            item.navIcon,
                            contentDescription = stringResource(item.label),
                            tint = Color.White
                        )
                        if (isSelected) {
                            Spacer(Modifier.width(6.dp))
                            Text(
                                stringResource(item.label),
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

// Previews

@Preview(showBackground = true)
@Composable
fun HomeTopBarPreview() {
    HombreCamionTheme {
        HomeTopBar(
            userName = "Miguel",
            planType = PaymentPlanType.Complete,
            plates = "4521-KBX",
            onNotificationsClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun VehiclePerformanceCardPreview() {
    val stats = listOf(
        VehicleStat(Icons.Outlined.LocalShipping.asIcon(), "50", "km/lts", "Rendimiento"),
        VehicleStat(Icons.Outlined.GpsFixed.asIcon(), "1000", "km/mm", "Desgaste"),
        VehicleStat(Icons.Outlined.Cloud.asIcon(), "100", "kg", "Emisión CO2")
    )
    HombreCamionTheme {
        VehiclePerformanceCard(
            vehicleName = "Mercedes Actros",
            plate = "4521-KBX",
            periodLabel = "ESTA SEMANA",
            stats = stats
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AlertCardPreview() {
    val alert = AlertUi(
        icon = R.drawable.tire_pressure_warning.asIcon(),
        title = "TPMS · eje delantero izq.",
        detailLabel = "Presión:",
        detailValue = "2.1 bar",
        detailExtra = "(mín. 6.5)",
        status = AlertStatus.CRITICA
    )
    HombreCamionTheme {
        AlertCard(alert = alert)
    }
}

@Preview(showBackground = true)
@Composable
fun WeatherCardPreview() {
    HombreCamionTheme {
        WeatherCard(
            temp = "34°",
            city = "Madrid",
            description = "Despejado · viento 12 km/h",
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SectionsGridPreview() {
    val sections = listOf(
        SectionItem(Icons.Outlined.GpsFixed.asIcon(), R.string.registrar_vehiculo, ""),
        SectionItem(Icons.Filled.WaterDrop.asIcon(), R.string.monitor, ""),
        SectionItem(Icons.Outlined.QueryStats.asIcon(), R.string.analytics, ""),
        SectionItem(Icons.AutoMirrored.Filled.Article.asIcon(), R.string.foro, ""),
        SectionItem(Icons.Filled.LocalOffer.asIcon(), R.string.promociones_descuentos, ""),
        SectionItem(Icons.Filled.Settings.asIcon(), R.string.configuracion, "")
    )
    HombreCamionTheme {
        SectionsGrid(sections = sections, onSectionClick = {})
    }
}

@Preview(showBackground = true)
@Composable
fun BlogPostCardPreview() {
    val post = BlogPost(
        category = "MANTENIMIENTO",
        title = "5 señales de desgaste irregular en llantas",
        excerpt = "Aprende a detectar a tiempo el desgaste que puede costarte un pinchazo en carretera…"
    )
    HombreCamionTheme {
        BlogPostCard(post = post)
    }
}

@Preview(showBackground = true)
@Composable
fun HomeBottomBarPreview() {
    HombreCamionTheme {
        HomeBottomBar(selected = BottomNavItems.HOME, onItemClick = {})
    }
}
