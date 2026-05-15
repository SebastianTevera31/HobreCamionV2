package com.rfz.appflotal.presentation.ui.forums.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.rfz.appflotal.presentation.theme.Dimens
import com.rfz.appflotal.presentation.theme.HombreCamionTheme

@Composable
fun SearchBlogBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier.fillMaxWidth().background(MaterialTheme.colorScheme.primary)) {
        OutlinedTextField(
            value = query, onValueChange = onQueryChange,
            placeholder = { Text(text = "Buscar", color = Color.White) },
            leadingIcon = {
                IconButton(onClick = onSearch)
                {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar",
                        tint = Color.White
                    )
                }
            },
            shape = MaterialTheme.shapes.large,
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedContainerColor = Color.White.copy(alpha = 0.14f),
                unfocusedContainerColor = Color.White.copy(alpha = 0.14f),
            ),
            modifier = Modifier.fillMaxWidth().padding(Dimens.PaddingExtraLarge)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SearchBlogBarPreview() {
    HombreCamionTheme {
        SearchBlogBar(
            query = "",
            onQueryChange = {},
            onSearch = {}
        )
    }
}