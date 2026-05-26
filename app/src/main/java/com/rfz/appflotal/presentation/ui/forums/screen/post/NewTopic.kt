package com.rfz.appflotal.presentation.ui.forums.screen.post

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.rfz.appflotal.R
import com.rfz.appflotal.presentation.theme.Dimens
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.utils.LoadState

@Composable
fun NewTopicScreen(
    modifier: Modifier = Modifier,
    newTopicStatus: LoadState<Unit>,
    onSend: (String, String, String, String) -> Unit,
    onCancel: () -> Unit = {},
    onBack: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var currentTag by remember { mutableStateOf("") }
    val tagsList = remember { mutableStateListOf<String>() }

    val fixedColors = listOf(
        "#F44336", "#E91E63", "#9C27B0", "#673AB7",
        "#3F51B5", "#2196F3", "#03A9F4", "#00BCD4",
        "#009688", "#4CAF50", "#8BC34A", "#CDDC39",
        "#FFEB3B", "#FFC107", "#FF9800", "#FF5722"
    )
    var selectedColor by remember { mutableStateOf(fixedColors[0]) }

    val isLoading = newTopicStatus is LoadState.Loading

    LaunchedEffect(newTopicStatus) {
        if (newTopicStatus is LoadState.Success) {
            onBack()
        }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .padding(Dimens.PaddingMedium)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall)
        ) {
            Text(
                text = stringResource(R.string.forum_new_topic_title),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )

            // Título
            Text(
                text = stringResource(R.string.forum_title_label),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                shape = RoundedCornerShape(Dimens.PaddingSmall),
                placeholder = { Text(stringResource(R.string.forum_title_placeholder)) },
                singleLine = true,
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            // Color Selection
            Text(
                text = stringResource(R.string.forum_select_color),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(fixedColors) { colorHex ->
                    val isSelected = selectedColor == colorHex
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(colorHex.toColorInt()))
                            .border(
                                width = if (isSelected) 3.dp else 0.dp,
                                color = if (isSelected) Color.Black else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable(enabled = !isLoading) { selectedColor = colorHex }
                    )
                }
            }

            // Tags
//            Text(
//                text = stringResource(R.string.forum_tags_label),
//                style = MaterialTheme.typography.labelLarge,
//                color = MaterialTheme.colorScheme.primary
//            )
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall)
//            ) {
//                OutlinedTextField(
//                    value = currentTag,
//                    onValueChange = { currentTag = it },
//                    shape = RoundedCornerShape(Dimens.PaddingSmall),
//                    placeholder = { Text(stringResource(R.string.forum_new_tag_placeholder)) },
//                    singleLine = true,
//                    enabled = !isLoading,
//                    modifier = Modifier.weight(1f)
//                )
//                IconButton(
//                    onClick = {
//                        if (currentTag.isNotBlank()) {
//                            tagsList.add(currentTag.trim())
//                            currentTag = ""
//                        }
//                    },
//                    enabled = !isLoading,
//                    modifier = Modifier
//                        .background(
//                            if (isLoading) Color.Gray else MaterialTheme.colorScheme.primary,
//                            CircleShape
//                        )
//                        .size(48.dp)
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.Add,
//                        contentDescription = stringResource(R.string.forum_add_tag_desc),
//                        tint = Color.White
//                    )
//                }
//            }

            // Display added tags
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingExtraSmall),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(tagsList) { tag ->
                    InputChip(
                        selected = false,
                        onClick = { if (!isLoading) tagsList.remove(tag) },
                        label = { Text(tag) },
                        trailingIcon = {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Eliminar",
                                modifier = Modifier.size(InputChipDefaults.IconSize)
                            )
                        }
                    )
                }
            }

            // Mensaje
            Text(
                text = stringResource(R.string.forum_message_label),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                shape = RoundedCornerShape(Dimens.PaddingSmall),
                placeholder = { Text(stringResource(R.string.forum_message_placeholder)) },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            if (newTopicStatus is LoadState.Error) {
                Text(
                    text = newTopicStatus.message,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (isLoading) {
                Button(
                    onClick = onCancel,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(Dimens.PaddingMedium),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Text(stringResource(R.string.forum_cancel_publication))
                    }
                }
            } else {
                Button(
                    onClick = {
                        val finalTags = tagsList.joinToString(",")
                        onSend(title, message, finalTags, selectedColor)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(Dimens.PaddingMedium),
                    enabled = title.isNotBlank() && message.isNotBlank()
                ) {
                    Text(stringResource(R.string.forum_publish_topic_button))
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NewTopicPreview() {
    HombreCamionTheme {
        NewTopicScreen(
            modifier = Modifier.safeContentPadding(),
            newTopicStatus = LoadState.Success(Unit),
            onSend = { _, _, _, _ -> },
            onBack = {}
        )
    }
}
