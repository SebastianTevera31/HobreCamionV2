package com.rfz.appflotal.presentation.ui.forums.components.scaffold

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.presentation.theme.Dimens
import com.rfz.appflotal.presentation.theme.HombreCamionTheme

data class ForumTopBarConfig(
    val title: String,
    val subtitle: String? = null,
    val showBackButton: Boolean = false,
    val showMenuButton: Boolean = false,
    val showPublishButton: Boolean = false,
    val isPublishing: Boolean = false,
    val message: String? = null,
    val searchConfig: ForumSearchConfig? = null,
    val onBackClick: (() -> Unit)? = null,
    val onMenuClick: (() -> Unit)? = null,
    val onPublishClick: (() -> Unit)? = null,
    val onCancelClick: (() -> Unit)? = null,
)

data class ForumSearchConfig(
    val value: String,
    val placeholder: String,
    val onValueChange: (String) -> Unit
)

@Composable
fun ForumModuleScaffold(
    topBarConfig: ForumTopBarConfig,
    modifier: Modifier = Modifier,
    floatingActionButton: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            ForumTopAppBar(
                config = topBarConfig
            )
        },
        floatingActionButton = floatingActionButton,
        bottomBar = bottomBar,
        content = content
    )
}

@Preview(showBackground = true)
@Composable
fun ForumScaffoldPreview() {
    HombreCamionTheme {
        ForumModuleScaffold(
            topBarConfig = ForumTopBarConfig(
                title = "Título del Foro",
                subtitle = "Subtítulo del Foro",
                showBackButton = true
            ),
            floatingActionButton = {
                FloatingActionButton(onClick = {}) {
                    Row(
                        modifier = Modifier.padding(Dimens.PaddingSmall),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add topic",
                            tint = Color.White
                        )
                        Text(
                            text = "Nuevo tema",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                }
            },
            bottomBar = {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding(),
                    tonalElevation = 2.dp
                ) {
                    BottomCommentField(
                        comment = "",
                        onCommentChange = {},
                        onSend = {},
                        selectedImage = null,
                        onAddImage = {},
                        onRemoveImage = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Contenido del Foro")
            }
        }
    }
}
