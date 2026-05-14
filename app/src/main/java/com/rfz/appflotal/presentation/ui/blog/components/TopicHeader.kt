package com.rfz.appflotal.presentation.ui.blog.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TireRepair
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.presentation.theme.Dimens
import com.rfz.appflotal.presentation.theme.HombreCamionTheme

@Composable
fun TopicHeader(
    title: String,
    content: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimens.PaddingMedium),
        colors = CardDefaults.cardColors(Color.White)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall),
            modifier = Modifier.padding(
                horizontal = Dimens.PaddingMedium,
                vertical = Dimens.PaddingSmall
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingExtraSmall)
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(Dimens.PaddingMedium))
                        .background(MaterialTheme.colorScheme.primary)
                ) {
                    Icon(
                        imageVector = Icons.Default.TireRepair,
                        contentDescription = null,
                        modifier = Modifier.align(
                            Alignment.Center
                        ),
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.padding(Dimens.PaddingExtraSmall))

                Column() {
                    Text(
                        text = "Tema",
                        style = MaterialTheme.typography.titleMedium.copy(color = Color.DarkGray)
                    )
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }

            }
            Text(
                text = content,
                style = MaterialTheme.typography.bodyLarge.copy(Color.DarkGray)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TopicHeaderPreview() {
    HombreCamionTheme {
        TopicHeader(
            title = "Mantenimiento Preventivo",
            content = "Aprende cómo realizar un mantenimiento preventivo a tus neumáticos para alargar su vida útil y ahorrar costos."
        )
    }
}