package com.rfz.appflotal.presentation.ui.forums.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Surface
import androidx.compose.ui.tooling.preview.Preview
import com.rfz.appflotal.presentation.theme.HombreCamionTheme

@Composable
fun ForumSearchBar(
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp),
        placeholder = {
            Text(
                text = placeholder,
                color = Color.White.copy(alpha = 0.55f),
                maxLines = 1
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Buscar",
                tint = Color.White.copy(alpha = 0.8f)
            )
        },
        singleLine = true,
        shape = RoundedCornerShape(18.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White.copy(alpha = 0.18f),
            unfocusedContainerColor = Color.White.copy(alpha = 0.18f),
            disabledContainerColor = Color.White.copy(alpha = 0.18f),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            cursorColor = Color.White
        )
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun ForumSearchBarPreview() {
    HombreCamionTheme {
        Surface(color = Color.DarkGray) {
            ForumSearchBar(
                value = "",
                placeholder = "Buscar en el foro...",
                onValueChange = {}
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun ForumSearchBarWithTextPreview() {
    HombreCamionTheme {
        Surface(color = Color.DarkGray) {
            ForumSearchBar(
                value = "Mecánica",
                placeholder = "Buscar en el foro...",
                onValueChange = {}
            )
        }
    }
}
