package com.rfz.appflotal.presentation.ui.home.screen.completeplan.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.GpsFixed
import androidx.compose.material.icons.outlined.OilBarrel
import androidx.compose.material.icons.outlined.QueryStats
import androidx.compose.ui.graphics.vector.ImageVector
import com.rfz.appflotal.R
import com.rfz.appflotal.core.util.screens.HombreCamionScreens
import com.rfz.appflotal.core.util.screens.NavScreens
import com.rfz.appflotal.presentation.ui.couponbook.navigation.CouponGraph
import com.rfz.appflotal.presentation.ui.forums.navigation.ForumsGraph
import com.rfz.appflotal.presentation.ui.inicio.ui.PaymentPlanType
import com.rfz.appflotal.presentation.ui.reportes.navigation.ReportGraph

sealed class IconResource {
    data class Vector(val imageVector: ImageVector) : IconResource()
    data class Drawable(@DrawableRes val resId: Int) : IconResource()
}

fun ImageVector.asIcon() = IconResource.Vector(this)
fun Int.asIcon() = IconResource.Drawable(this)

data class VehicleStat(
    val id: Int,
    val icon: IconResource,
    val value: String,
    val unit: String,
    @StringRes val label: Int
)

data class AlertUi(
    val icon: IconResource,
    val title: String,
    val detailLabel: String,
    val detailValue: String,
    val detailExtra: String? = null,
    val status: AlertStatus
)

enum class AlertStatus { CRITICA, PENDIENTE }

data class SectionItem(val icon: IconResource, @StringRes val label: Int, val route: Any)
data class BlogPost(
    val linkImage: String = "",
    val categories: List<String>,
    val title: String,
    val excerpt: String
)

data class CompletePlanUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val userName: String = "",
    val vehicleName: String = "",
    val vehiclePlate: String = "",
    val paymentPlanType: PaymentPlanType = PaymentPlanType.Complete,
    val stats: List<VehicleStat> = listOf(
        VehicleStat(
            1,
            Icons.Outlined.OilBarrel.asIcon(),
            "0",
            "lts",
            R.string.consumo_de_combustible
        ),
        VehicleStat(
            2,
            Icons.Outlined.Cloud.asIcon(), "100", "kg",
            R.string.emision_co2
        )
    ),
    val alerts: List<AlertUi> = listOf(
        AlertUi(
            icon = R.drawable.tire_pressure_warning.asIcon(),
            title = "TPMS · eje delantero izq.",
            detailLabel = "Presión:",
            detailValue = "2.1 bar",
            detailExtra = "(mín. 6.5)",
            status = AlertStatus.CRITICA
        ),
        AlertUi(
            icon = Icons.Outlined.GpsFixed.asIcon(),
            title = "Alerta de mm bajo",
            detailLabel = "Profundidad baja ·",
            detailValue = "4 mm",
            status = AlertStatus.PENDIENTE
        )
    ),
    val weatherTemp: String = "34°",
    val weatherCity: String = "Madrid",
    @StringRes val weatherDesc: Int = R.string.clearsky,
    val sections: List<SectionItem> = listOf(
        SectionItem(
            icon = Icons.Outlined.GpsFixed.asIcon(),
            label = R.string.registrar_vehiculo,
            route = NavScreens.REGISTRO_LLANTAS
        ),
        SectionItem(
            icon = R.drawable.tire_pressure_warning.asIcon(),
            label = R.string.monitor,
            route = HombreCamionScreens.MONITOR.name
        ),
        SectionItem(
            icon = Icons.Outlined.QueryStats.asIcon(),
            label = R.string.analytics,
            route = ReportGraph
        ),
        SectionItem(
            icon = Icons.AutoMirrored.Filled.Article.asIcon(),
            label = R.string.foro,
            route = ForumsGraph
        ),
        SectionItem(
            icon = Icons.Filled.LocalOffer.asIcon(),
            label = R.string.promociones_descuentos,
            route = CouponGraph
        ),
        SectionItem(
            icon = R.drawable.tire_register.asIcon(),
            label = R.string.registrar,
            route = HombreCamionScreens.REGISTER_TIRES.name
        )
    ),
    val blogPosts: List<BlogPost> = emptyList()
)
