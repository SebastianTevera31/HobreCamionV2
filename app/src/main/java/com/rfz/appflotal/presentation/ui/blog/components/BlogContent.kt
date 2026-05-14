package com.rfz.appflotal.presentation.ui.blog.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.presentation.theme.Dimens
import com.rfz.appflotal.presentation.theme.HombreCamionTheme

@Composable
fun BlogContent(
    title: String,
    content: String,
    modifier: Modifier = Modifier,
    isAuthor: Boolean = false,
    isPost: Boolean = false,
    showAllContent: Boolean = true,
    style: TextStyle = MaterialTheme.typography.titleLarge,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall)
        ) {
            if (isPost) {
                Icon(
                    imageVector = Icons.Default.Pin,
                    contentDescription = null,
                    tint = Color(0xFFFFD700)
                )
            }
            Text(
                text = title,
                style = style.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                ),
                maxLines = if (showAllContent) Int.MAX_VALUE else 2,
                overflow = if (showAllContent) TextOverflow.Clip else TextOverflow.Ellipsis
            )
            if (isAuthor) {
                Box(
                    modifier = Modifier
                        .background(Color(0xFFE8F5E9), RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "AUTOR",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4CAF50)
                        )
                    )
                }
            }
        }

        Text(
            text = content,
            style = MaterialTheme.typography.bodyLarge.copy(Color.DarkGray),
            maxLines = if (showAllContent) Int.MAX_VALUE else 2,
            overflow = if (showAllContent) TextOverflow.Clip else TextOverflow.Ellipsis
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
            isPost = true
        )
    }
}
