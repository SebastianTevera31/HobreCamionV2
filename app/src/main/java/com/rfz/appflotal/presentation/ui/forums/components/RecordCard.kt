package com.rfz.appflotal.presentation.ui.forums.components

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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.TireRepair
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.rfz.appflotal.R
import com.rfz.appflotal.presentation.theme.Dimens
import com.rfz.appflotal.presentation.theme.HombreCamionTheme

@Composable
fun RecordCard(
    header: String,
    time: String,
    isComment: Boolean,
    isSaved: Boolean,
    modifier: Modifier = Modifier,
    firstInitial: String = "",
    secondInitial: String = "",
    onSave: () -> Unit = {},
    onSeeMore: () -> Unit = {},
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimens.PaddingMedium),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onSeeMore
    ) {
        Column(
            modifier = Modifier.padding(Dimens.PaddingMedium),
            verticalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall)
        ) {
            Row(
                verticalAlignment = Alignment.Top, // Alineado arriba para contenido multilínea
                horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall)
            ) {
                if (isComment) {
                    if (firstInitial.isNotEmpty()) {
                        Surface( // Usamos Surface para elevación y forma más limpia
                            modifier = Modifier.size(40.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = if (secondInitial.isNotEmpty()) "$firstInitial$secondInitial" else firstInitial,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }
                } else {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.TireRepair,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }


                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall)
                    ) {
                        Text(
                            text = header,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (!isComment) {
                            Icon(
                                painter = painterResource(R.drawable.keep_24px),
                                contentDescription = "Pinned",
                                tint = Color(0xFFFBC02D),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.size(4.dp))
                        }
                        Text(
                            text = time,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                // Botón de Like
                Surface(
                    onClick = onSave,
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSaved) Color.Red.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant.copy(
                        alpha = 0.5f
                    ),
                    contentColor = if (isSaved) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = if (isSaved) Icons.Default.Favorite else Icons.Outlined.Favorite,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RecordCardPreview() {
    HombreCamionTheme {
        RecordCard(
            header = "Juan Pérez",
            time = "Hace 2 horas",
            isComment = true,
            isSaved = false,
            firstInitial = "J",
            secondInitial = "P"
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RecordCardWithImagePreview() {
    HombreCamionTheme {
        RecordCard(
            header = "Maria Garcia",
            time = "Hace 5 horas",
            isComment = false,
            isSaved = true,
            firstInitial = "M",
            secondInitial = "G"
        )
    }
}
