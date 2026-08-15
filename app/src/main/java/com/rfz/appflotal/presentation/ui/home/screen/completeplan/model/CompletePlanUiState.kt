package com.rfz.appflotal.presentation.ui.home.screen.completeplan.model

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.GpsFixed
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.QueryStats
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.ui.graphics.vector.ImageVector
import com.rfz.appflotal.R
import com.rfz.appflotal.core.util.screens.HombreCamionScreens
import com.rfz.appflotal.core.util.screens.NavScreens
import com.rfz.appflotal.presentation.ui.couponbook.navigation.CouponGraph
import com.rfz.appflotal.presentation.ui.forums.navigation.ForumsGraph
import com.rfz.appflotal.presentation.ui.inicio.ui.PaymentPlanType
import com.rfz.appflotal.presentation.ui.reportes.navigation.ReportGraph

data class VehicleStat(
    val icon: ImageVector,
    val value: String,
    val unit: String,
    val label: String
)

data class AlertUi(
    val icon: ImageVector,
    val title: String,
    val detailLabel: String,
    val detailValue: String,
    val detailExtra: String? = null,
    val status: AlertStatus
)

enum class AlertStatus { CRITICA, PENDIENTE }

data class SectionItem(val icon: ImageVector, @StringRes val label: Int, val route: Any)

data class BlogPost(val category: String, val title: String, val excerpt: String)

data class CompletePlanUiState(
    val currentScreen: Int = 0,
    val userName: String = "Miguel",
    val vehicleName: String = "Mercedes Actros",
    val vehiclePlate: String = "4521-KBX",
    val paymentPlanType: PaymentPlanType = PaymentPlanType.Complete,
    val periodLabel: String = "ESTA SEMANA",
    val stats: List<VehicleStat> = listOf(
        VehicleStat(Icons.Outlined.LocalShipping, "50", "km/lts", "Rendimiento"),
        VehicleStat(Icons.Outlined.GpsFixed, "1000", "km/mm", "Desgaste"),
        VehicleStat(Icons.Outlined.Cloud, "100", "kg", "Emisión CO2")
    ),
    val alerts: List<AlertUi> = listOf(
        AlertUi(
            icon = Icons.Outlined.Warning,
            title = "TPMS · eje delantero izq.",
            detailLabel = "Presión:",
            detailValue = "2.1 bar",
            detailExtra = "(mín. 6.5)",
            status = AlertStatus.CRITICA
        ),
        AlertUi(
            icon = Icons.Outlined.GpsFixed,
            title = "Alerta de mm bajo",
            detailLabel = "Profundidad baja ·",
            detailValue = "4 mm",
            status = AlertStatus.PENDIENTE
        )
    ),
    val weatherTemp: String = "34°",
    val weatherCity: String = "Madrid",
    val weatherDesc: String = "Despejado · viento 12 km/h",
    val sections: List<SectionItem> = listOf(
        SectionItem(
            icon = Icons.Outlined.GpsFixed,
            label = R.string.registrar_vehiculo,
            route = NavScreens.REGISTRO_LLANTAS
        ),
        SectionItem(
            icon = Icons.Filled.WaterDrop,
            label = R.string.monitor,
            route = HombreCamionScreens.MONITOR.name
        ),
        SectionItem(
            icon = Icons.Outlined.QueryStats,
            label = R.string.analytics,
            route = ReportGraph
        ),
        SectionItem(
            icon = Icons.AutoMirrored.Filled.Article,
            label = R.string.foro,
            route = ForumsGraph
        ),
        SectionItem(
            icon = Icons.Filled.LocalOffer,
            label = R.string.promociones_descuentos,
            route = CouponGraph
        ),
        SectionItem(
            icon = Icons.Filled.Settings,
            label = R.string.configuracion,
            route = NavScreens.INFORMACION_USUARIO
        )
    ),
    val blogPosts: List<BlogPost> = listOf(
        BlogPost(
            category = "MANTENIMIENTO",
            title = "5 señales de desgaste irregular en llantas",
            excerpt = "Aprende a detectar a tiempo el desgaste que puede costarte un pinchazo en carretera…"
        ),
        BlogPost(
            category = "CONSUMO",
            title = "Cómo bajar tu consumo un 10% este verano",
            excerpt = "Presión, velocidad y climatización: tres ajustes sencillos que notarás en el depósito…"
        )
    )
)
