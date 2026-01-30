package com.rfz.appflotal.presentation.ui.home.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.R
import com.rfz.appflotal.data.NetworkStatus
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.theme.primaryLight
import com.rfz.appflotal.presentation.ui.home.utils.cardBackground
import com.rfz.appflotal.presentation.ui.home.utils.menuItems
import com.rfz.appflotal.presentation.ui.home.utils.primaryColor
import com.rfz.appflotal.presentation.ui.home.utils.secondaryColor
import com.rfz.appflotal.presentation.ui.inicio.ui.PaymentPlanType
import java.time.LocalDate

@Composable
fun CompletePlanContent(
    paymentPlan: PaymentPlanType,
    userName: String,
    plates: String,
    wifiStatus: NetworkStatus,
    modifier: Modifier = Modifier,
    onShowMonitorDialog: (Boolean) -> Unit,
    onNavigate: (route: String) -> Unit
) {
    val context = LocalContext.current

    Box(modifier = modifier) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(primaryColor, primaryLight),
                            startY = 0f,
                            endY = 500f
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
                UserHeader(
                    paymentPlan = paymentPlan,
                    userName = userName,
                    plates = plates
                ) {
                    if (wifiStatus == NetworkStatus.Connected) {
                        onShowMonitorDialog(true)
                    } else Toast.makeText(
                        context,
                        R.string.error_conexion_internet,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxSize()
            ) {
                items(menuItems) { item ->
                    ElegantMenuCard(
                        title = stringResource(item.title),
                        iconRes = item.iconRes,
                        onClick = { onNavigate(item.route) },
                        primaryColor = primaryColor,
                        secondaryColor = secondaryColor,
                        cardBackground = cardBackground
                    )
                }
            }
        }


        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(56.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(primaryColor, secondaryColor)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(
                    R.string.copyright,
                    LocalDate.now().year
                ),
                color = Color.White.copy(alpha = 0.95f),
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CompletePlanContentPreview() {
    HombreCamionTheme {
        CompletePlanContent(
            paymentPlan = PaymentPlanType.Complete,
            userName = "Juan Perez",
            plates = "ABC-123",
            wifiStatus = NetworkStatus.Connected,
            onShowMonitorDialog = {},
            onNavigate = {}
        )
    }
}
