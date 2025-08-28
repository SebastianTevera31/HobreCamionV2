package com.rfz.appflotal.presentation.ui.home.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.R
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.theme.primaryLight
import com.rfz.appflotal.presentation.ui.inicio.ui.PaymentPlanType
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun UserHeader(
    paymentPlan: PaymentPlanType,
    userName: String,
    modifier: Modifier = Modifier,
    showDialog: () -> Unit
) {
    Row(
        modifier = modifier
            .background(primaryLight)
            .fillMaxSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val horizontalOrientation = if (paymentPlan == PaymentPlanType.Complete)
            Alignment.CenterHorizontally else Alignment.Start

        Column(
            modifier = Modifier,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = horizontalOrientation
        ) {
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

        if (paymentPlan != PaymentPlanType.Complete) {
            IconButton(onClick = showDialog) {
                Icon(
                    painter = painterResource(R.drawable.macconfig),
                    contentDescription = null,
                    modifier = Modifier.size(100.dp),
                    tint = Color.White

                )
            }

        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
@Preview()
fun UserTitlePreview() {
    HombreCamionTheme {
        UserHeader(paymentPlan = PaymentPlanType.OnlyTpms, userName = "FK") {}
    }
}