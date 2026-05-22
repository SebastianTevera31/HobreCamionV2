package com.rfz.appflotal.presentation.ui.forums.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.Surface
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rfz.appflotal.R
import com.rfz.appflotal.presentation.theme.Dimens
import com.rfz.appflotal.presentation.theme.HombreCamionTheme

@Composable
fun BlogContent(
    title: String,
    content: String,
    modifier: Modifier = Modifier,
    time: String = "",
    firstInitial: String = "",
    secondInitial: String = "",
    isAuthor: Boolean = false,
    isPost: Boolean = false,
    showAllContent: Boolean = true,
    style: TextStyle = MaterialTheme.typography.titleMedium, // Mejorado de titleLarge para consistencia
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.PaddingExtraSmall) // Espaciado más compacto
    ) {
        Row(
            verticalAlignment = Alignment.Top, // Alineado arriba para contenido multilínea
            horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall)
        ) {
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

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall)
                ) {
                    Text(
                        text = title,
                        style = style.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        maxLines = if (showAllContent) Int.MAX_VALUE else 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (isAuthor) {
                        Surface(
                            color = Color(0xFFE8F5E9),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "AUTOR",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF2E7D32),
                                    fontSize = 9.sp
                                )
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isPost) {
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
        }

        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp
            ),
            maxLines = if (showAllContent) Int.MAX_VALUE else 3,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BlogContentPreview() {
    HombreCamionTheme {
        BlogContent(
            title = "Título de prueba",
            content = "Este es un contenido de prueba para el blog que permite visualizar cómo se renderiza el componente BlogContent.",
            isAuthor = true,
            isPost = true,
            firstInitial = "H",
            secondInitial = "2",
            time = "3 horas"
        )
    }
}
