package com.rfz.appflotal.presentation.ui.forums.components.scaffold

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rfz.appflotal.R
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.forums.components.ForumSearchBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForumTopAppBar(
    config: ForumTopBarConfig,
    modifier: Modifier = Modifier
) {
    var expandMenu by remember { mutableStateOf(false) }

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
        TopAppBar(
            title = {
                Column(
                    modifier = Modifier
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
            },
            navigationIcon = {
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
            },
            actions = {
                if (config.showMenuButton) {
                    Column {
                        IconButton(
                            onClick = {
                                expandMenu = true
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Más opciones",
                                tint = Color.White
                            )
                        }

                        DropdownMenu(expanded = expandMenu, onDismissRequest = {}) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "Gustados"
                                    )
                                },
                                onClick = {
                                    config.onMenuClick?.invoke()
                                }
                            )
                        }
                    }
                }

                if (config.showPublishButton) {
                    if (config.isPublishing) {
                        ElevatedButton(
                            onClick = {
                                config.onCancelClick?.invoke()
                            }) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                                Text(
                                    text = stringResource(R.string.forum_cancel_publication),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                        }
                    } else {
                        ElevatedButton(
                            onClick = {
                                config.onPublishClick?.invoke()
                            }) {
                            Text(
                                text = stringResource(R.string.publicar),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
        )

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
                showMenuButton = true,
                showPublishButton = true,
                searchConfig = ForumSearchConfig(
                    value = "",
                    placeholder = "Buscar temas...",
                    onValueChange = {}
                )
            )
        )
    }
}
