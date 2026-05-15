package com.rfz.appflotal.presentation.ui.blog.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.icons.filled.Favorite
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.R
import com.rfz.appflotal.presentation.theme.Dimens
import com.rfz.appflotal.presentation.theme.HombreCamionTheme

@Composable
fun CommentCard(
    firstInitial: String,
    user: String,
    content: String,
    imageUrl: String,
    likes: Int,
    modifier: Modifier = Modifier,
    isSaved: Boolean = false,
    onSeeMore: () -> Unit = {},
    onReply: () -> Unit = {},
    onSave: () -> Unit = {},
    secondInitial: String = "",
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(Dimens.PaddingMedium),
        colors = CardDefaults.cardColors(Color.White),
        onClick = onSeeMore
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
                horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingMedium)
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(100))
                        .background(MaterialTheme.colorScheme.primary)
                        .align(Alignment.Top)
                ) {
                    Text(
                        text = if (secondInitial.isNotEmpty()) "$firstInitial$secondInitial" else firstInitial,
                        modifier = Modifier.align(
                            Alignment.Center
                        ),
                        style = MaterialTheme.typography.titleLarge.copy(color = Color.White)
                    )
                }
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(Dimens.PaddingLarge)
                ) {
                    BlogContent(
                        title = user,
                        content = content
                    )

                    if (imageUrl.isNotEmpty()) {
                        Image(
                            painter = painterResource(R.drawable.logo),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(Dimens.PaddingMedium))
                                .background(MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }

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
                            Text(text = likes.toString())
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall),
                            modifier = Modifier.clickable { onReply() }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Reply,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(text = "Responder", color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CommentCardPreview() {
    HombreCamionTheme {
        CommentCard(
            firstInitial = "J",
            user = "Juan Perez",
            content = "Este es un comentario de prueba.",
            imageUrl = "",
            likes = 12,
            isSaved = false,
            secondInitial = "P"
        )
    }
}
