package com.rfz.appflotal.presentation.commons

import android.content.Intent
import android.graphics.fonts.FontFamily
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import androidx.core.net.toUri
import com.rfz.appflotal.R
import com.rfz.appflotal.core.util.AppLocale
import com.rfz.appflotal.presentation.theme.FiraMono
import com.rfz.appflotal.presentation.theme.HombreCamionTheme

@Composable
fun UpdateAppScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    val currentLanguage = AppLocale.currentLocale.collectAsState()
    val imageGooglePlay = if (currentLanguage.value.language.startsWith("es")) {
        R.drawable.getitongoogleplay_badge_web_color_spanish_latam
    } else R.drawable.getitongoogleplay_badge_web_color_english

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.medium_dimen))
    ) {
        Text(
            text = "Actualizacion disponible".uppercase(),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Image(painter = painterResource(id = R.drawable.update_icon), contentDescription = null)
        Text(
            text = "Hay una nueva version disponible. Actualiza para continuar usando la app.",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.large_dimen)))

        IconButton(
            onClick = {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    "https://play.google.com/store/apps/details?id=com.rfz.appflotal".toUri()
                )
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
            }, modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
        ) {
            Image(
                painter = painterResource(id = imageGooglePlay),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun MaintenanceAppScreen(date: String, modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.medium_dimen))
    ) {
        Text(
            text = "En Mantenimiento".uppercase(),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            fontFamily = FiraMono
        )
        Image(
            painter = painterResource(id = R.drawable.maintenance_icon),
            contentDescription = null
        )
        Text(
            text = "Sentimos los incovenientes. Estamos atendiendo mantenciones al sistema para garantizar un buen funcionamiento de la app.",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            fontFamily = FiraMono
        )

    }
}

@Composable
fun ScheduledMaintenanceSnackBanner(
    visible: Boolean,
    message: String,
    modifier: Modifier = Modifier,
    containerColor: Color = Color("#FFF3E0".toColorInt()),
    contentColor: Color = Color("#D84315".toColorInt())
) {
    val density = LocalDensity.current

    // Se monta/desmonta con animación de entrada/salida desde el borde superior
    Box(modifier.fillMaxWidth()) {
        AnimatedVisibility(
            visible = visible,
            enter = slideInVertically(
                animationSpec = tween(2000)
            ) { fullHeight -> -fullHeight } + expandVertically(
                expandFrom = Alignment.Top
            ) + fadeIn(initialAlpha = 0.3f),
            exit = slideOutVertically(animationSpec = tween(2000)) { with(density) { -40.dp.roundToPx() } } + shrinkVertically() + fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(horizontal = 8.dp, vertical = 8.dp)
        ) {
            Surface(
                color = containerColor,
                contentColor = contentColor,
                shape = MaterialTheme.shapes.medium,
                tonalElevation = 3.dp,
                shadowElevation = 3.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 800.dp)
            ) {
                Row(
                    Modifier
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Info, contentDescription = null)
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true, showSystemUi = true, locale = "es")
@Composable
fun UpdateAppScreenPreview() {
    HombreCamionTheme {
        UpdateAppScreen()
    }

}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MaintenanceScreenPreview() {
    HombreCamionTheme {
        MaintenanceAppScreen(
            date = "",
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ScheduledMaintenancePreview() {
    HombreCamionTheme {
        ScheduledMaintenanceSnackBanner(
            visible = true,
            message = "Mensaje de prueba"
        )
    }
}