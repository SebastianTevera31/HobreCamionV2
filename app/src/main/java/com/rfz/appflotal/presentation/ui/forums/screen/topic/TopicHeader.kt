package com.rfz.appflotal.presentation.ui.forums.screen.topic

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.TireRepair
import androidx.compose.material.icons.outlined.Favorite
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.rfz.appflotal.presentation.theme.Dimens
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.forums.components.PostDropdownMenu

@Composable
fun TopicHeader(
    title: String,
    content: String,
    onReport: () -> Unit,
    onSave: () -> Unit,
    isSaved: Boolean,
    likes: Int,
    modifier: Modifier = Modifier,
    imageUrl: String = ""
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
                horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingExtraSmall)
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.Top)
                        .size(60.dp)
                        .clip(RoundedCornerShape(Dimens.PaddingMedium))
                        .background(MaterialTheme.colorScheme.primary)
                ) {
                    if (imageUrl.isNotEmpty()) {
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.TireRepair,
                            contentDescription = null,
                            modifier = Modifier.align(
                                Alignment.Center
                            ),
                            tint = Color.White
                        )
                    }
                }
                Spacer(modifier = Modifier.padding(Dimens.PaddingExtraSmall))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Tema",
                        style = MaterialTheme.typography.titleMedium.copy(color = Color.DarkGray)
                    )
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }

                PostDropdownMenu(onReport = onReport)
            }
            Text(
                text = content,
                style = MaterialTheme.typography.bodySmall.copy(Color.DarkGray)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingMedium)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall),
                    modifier = Modifier.clickable { onSave() }
                ) {
                    Icon(
                        imageVector = if (isSaved) Icons.Default.Favorite else Icons.Outlined.Favorite,
                        contentDescription = null,
                        tint = if (isSaved) Color.Red else Color.Gray
                    )
                    Text(
                        text = likes.toString(),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TopicHeaderPreview() {
    HombreCamionTheme {
        TopicHeader(
            title = "Mantenimiento Preventivo",
            content = "Aprende cómo realizar un mantenimiento preventivo a tus neumáticos para alargar su vida útil y ahorrar costos.",
            onReport = {},
            onSave = {},
            isSaved = true,
            likes = 30,
            imageUrl = ""
        )
    }
}