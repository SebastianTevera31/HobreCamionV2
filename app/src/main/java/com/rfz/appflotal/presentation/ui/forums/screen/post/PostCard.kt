package com.rfz.appflotal.presentation.ui.forums.screen.post

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.TireRepair
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import coil.compose.AsyncImage
import com.rfz.appflotal.presentation.theme.Dimens
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.forums.components.BlogContent

@Composable
fun PostCard(
    title: String,
    content: String,
    modifier: Modifier = Modifier,
    author: String = "",
    firstInitial: String = "",
    secondInitial: String = "",
    time: String = "",
    numComments: Int = 0,
    imageUrl: String = "",
    isPost: Boolean = false,
    showOptions: Boolean = false,
    hidePostInfo: Boolean = false,
    onNav: () -> Unit,
    onReport: () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(Dimens.PaddingMedium),
        colors = CardDefaults.cardColors(Color.White),
        onClick = onNav
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
                    if (imageUrl.isNotEmpty()) {
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
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
                BlogContent(
                    title = title,
                    content = content,
                    showAllContent = false,
                    style = if (isPost) MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f)
                )
                if (!isPost) {
                    IconButton(onClick = onNav) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                            contentDescription = null,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                if (showOptions) {
                    IconButton(onClick = onReport) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "See more",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            if (isPost && !hidePostInfo) {
                HorizontalDivider(thickness = 1.dp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(RoundedCornerShape(100))
                                .background(MaterialTheme.colorScheme.primary)
                                .align(Alignment.Top)
                        ) {
                            Text(
                                text = if (secondInitial.isNotEmpty()) "$firstInitial$secondInitial" else firstInitial,
                                modifier = Modifier.align(
                                    Alignment.Center
                                ),
                                style = MaterialTheme.typography.bodySmall.copy(color = Color.White)
                            )
                        }
                        Spacer(modifier = Modifier.padding(Dimens.PaddingExtraSmall))
                        Text(
                            text = author, style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                        Text(text = " · $time", color = Color.DarkGray)
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingExtraSmall),
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Comment,
                            contentDescription = null
                        )
                        Text(text = numComments.toString())
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PostCardPreview() {
    HombreCamionTheme {
        PostCard(
            title = "Título de ejemplo",
            content = "Este es un contenido de ejemplo para la tarjeta de publicación. Aquí se muestra cómo se vería el texto descriptivo.",
            author = "Juan Pérez",
            firstInitial = "J",
            secondInitial = "P",
            time = "Hace 2 horas",
            numComments = 12,
            isPost = true,
            showOptions = true,
            onNav = {},
            onReport = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PostCardForumPreview() {
    HombreCamionTheme {
        PostCard(
            title = "Título del Foro",
            content = "Este es un contenido de ejemplo para una entrada del foro que no es una publicación completa.",
            isPost = false,
            onNav = {},
            onReport = {}
        )
    }
}
