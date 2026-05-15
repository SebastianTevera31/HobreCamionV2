package com.rfz.appflotal.presentation.ui.forums.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rfz.appflotal.presentation.theme.HombreCamionTheme

@Composable
fun ForumTopAppBar(
    config: ForumTopBarConfig,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF25128C),
                        Color(0xFF4A8DEE)
                    )
                )
            )
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(
                start = 12.dp,
                end = 12.dp,
                top = 8.dp,
                bottom = 16.dp
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (config.showBackButton) {
                IconButton(
                    onClick = {
                        config.onBackClick?.invoke()
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Regresar",
                        tint = Color.White
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(
                        start = if (config.showBackButton) 4.dp else 0.dp,
                        end = 8.dp
                    )
            ) {
                Text(
                    text = config.title,
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                config.subtitle?.let { subtitle ->
                    Text(
                        text = subtitle,
                        color = Color.White.copy(alpha = 0.78f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            if (config.showMenuButton) {
                IconButton(
                    onClick = {
                        config.onMenuClick?.invoke()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreHoriz,
                        contentDescription = "Más opciones",
                        tint = Color.White
                    )
                }
            }
        }

        config.searchConfig?.let { searchConfig ->
            Spacer(modifier = Modifier.height(12.dp))

            ForumSearchBar(
                value = searchConfig.value,
                placeholder = searchConfig.placeholder,
                onValueChange = searchConfig.onValueChange
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ForumTopAppBarPreview() {
    HombreCamionTheme {
        ForumTopAppBar(
            config = ForumTopBarConfig(
                title = "Foros",
                subtitle = "Comunidad de transportistas",
                showBackButton = true
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ForumTopAppBarSearchPreview() {
    HombreCamionTheme {
        ForumTopAppBar(
            config = ForumTopBarConfig(
                title = "Buscar en el foro",
                showBackButton = true,
                searchConfig = ForumSearchConfig(
                    value = "",
                    placeholder = "Buscar temas...",
                    onValueChange = {}
                )
            )
        )
    }
}
