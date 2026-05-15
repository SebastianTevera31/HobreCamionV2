package com.rfz.appflotal.presentation.ui.forums.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.rfz.appflotal.presentation.theme.HombreCamionTheme

data class ForumTopBarConfig(
    val title: String,
    val subtitle: String? = null,
    val showBackButton: Boolean = false,
    val showMenuButton: Boolean = true,
    val searchConfig: ForumSearchConfig? = null,
    val onBackClick: (() -> Unit)? = null,
    val onMenuClick: (() -> Unit)? = null
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
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            ForumTopAppBar(
                config = topBarConfig
            )
        },
        floatingActionButton = floatingActionButton,
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
            )
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
