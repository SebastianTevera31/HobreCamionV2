package com.rfz.appflotal.presentation.ui.forums.screen.post

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import coil.compose.AsyncImage
import com.rfz.appflotal.R
import com.rfz.appflotal.presentation.theme.Dimens
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.forums.components.CharacterCounter
import com.rfz.appflotal.presentation.ui.forums.components.ForumLimits
import com.rfz.appflotal.presentation.ui.forums.screen.topic.ReplyEditor
import com.rfz.appflotal.presentation.ui.utils.LoadState

@Composable
fun NewTopicScreen(
    modifier: Modifier = Modifier,
    newTopicStatus: LoadState<Unit>,
    onBack: () -> Unit,
    selectedImage: Uri? = null,
    onAddImage: () -> Unit = {},
    onRemoveImage: () -> Unit = {},
    title: String = "",
    onTitleChange: (String) -> Unit = {},
    description: String = "",
    onDescriptionChange: (String) -> Unit = {},
    selectedColor: String = "#F44336",
    onColorChange: (String) -> Unit = {},
    tags: List<String> = emptyList(),
    onTagsChange: (List<String>) -> Unit = {},
    availableTags: List<String> = emptyList(),
    tagsState: LoadState<Unit> = LoadState.Idle,
    onRetryTags: () -> Unit = {}
) {
    val fixedColors = listOf(
        "#F44336", "#E91E63", "#9C27B0", "#673AB7",
        "#3F51B5", "#2196F3", "#03A9F4", "#00BCD4",
        "#009688", "#4CAF50", "#8BC34A", "#CDDC39",
        "#FFEB3B", "#FFC107", "#FF9800", "#FF5722"
    )

    val isLoading = newTopicStatus is LoadState.Loading

    LaunchedEffect(newTopicStatus) {
        if (newTopicStatus is LoadState.Success) {
            onBack()
        }
    }

    BackHandler {
        onRemoveImage()
        onBack()
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .padding(Dimens.PaddingMedium)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
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
                onValueChange = { onTitleChange(it.take(ForumLimits.TITLE_MAX_LENGTH)) },
                shape = RoundedCornerShape(Dimens.PaddingSmall),
                placeholder = { Text(stringResource(R.string.forum_title_placeholder)) },
                supportingText = {
                    CharacterCounter(current = title.length, max = ForumLimits.TITLE_MAX_LENGTH)
                },
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
                            .clickable(enabled = !isLoading) { onColorChange(colorHex) }
                    )
                }
            }

            // Tag Input
            var tagInput by remember { mutableStateOf("") }
            Text(
                text = stringResource(R.string.forum_tags_label),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = tagInput,
                    onValueChange = { tagInput = it },
                    shape = RoundedCornerShape(Dimens.PaddingSmall),
                    placeholder = { Text(stringResource(R.string.forum_new_tag_placeholder)) },
                    singleLine = true,
                    enabled = !isLoading,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = {
                        val newTag = tagInput.trim().uppercase()
                        if (newTag.isNotEmpty() && tags.none {
                                it.equals(
                                    newTag,
                                    ignoreCase = true
                                )
                            }) {
                            onTagsChange(tags + newTag)
                        }
                        tagInput = ""
                    },
                    enabled = !isLoading && tagInput.isNotBlank(),
                    modifier = Modifier
                        .background(
                            if (tagInput.isNotBlank()) MaterialTheme.colorScheme.primary else Color.Gray,
                            CircleShape
                        )
                        .size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.forum_add_tag_desc),
                        tint = Color.White
                    )
                }
            }

            // Display added tags
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingExtraSmall),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(tags) { tag ->
                    InputChip(
                        selected = false,
                        onClick = {
                            if (!isLoading) {
                                onTagsChange(tags.filter { it != tag })
                            }
                        },
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

            if (tags.isEmpty()) {
                Text(
                    text = stringResource(R.string.forum_tags_required_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            // Etiquetas sugeridas
            when {
                tagsState is LoadState.Loading -> CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )

                tagsState is LoadState.Error -> Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.forum_tags_load_error),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = onRetryTags, enabled = !isLoading) {
                        Text(stringResource(R.string.forum_tags_retry))
                    }
                }

                availableTags.isNotEmpty() -> {
                    var suggestionsExpanded by rememberSaveable { mutableStateOf(true) }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { suggestionsExpanded = !suggestionsExpanded }
                    ) {
                        Text(
                            text = stringResource(R.string.forum_suggested_tags_label),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Icon(
                            imageVector = if (suggestionsExpanded) {
                                Icons.Default.KeyboardArrowUp
                            } else {
                                Icons.Default.KeyboardArrowDown
                            },
                            contentDescription = stringResource(
                                if (suggestionsExpanded) {
                                    R.string.forum_collapse_tags_desc
                                } else {
                                    R.string.forum_expand_tags_desc
                                }
                            ),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    AnimatedVisibility(visible = suggestionsExpanded) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingExtraSmall),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            availableTags.forEach { suggestion ->
                                val isSelected =
                                    tags.any { it.equals(suggestion, ignoreCase = true) }
                                FilterChip(
                                    selected = isSelected,
                                    enabled = !isLoading,
                                    onClick = {
                                        onTagsChange(
                                            if (isSelected) {
                                                tags.filterNot {
                                                    it.equals(suggestion, ignoreCase = true)
                                                }
                                            } else {
                                                tags + suggestion
                                            }
                                        )
                                    },
                                    label = { Text(suggestion) }
                                )
                            }
                        }
                    }
                }
            }

            // Mensaje
            Text(
                text = stringResource(R.string.forum_message_label),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )

            if (selectedImage != null) {
                ImageThumbnail(
                    uri = selectedImage,
                    onRemove = onRemoveImage
                )
            }

            ReplyEditor(
                message = description,
                isLoading = isLoading,
                onMessageChange = onDescriptionChange,
                onAddImage = onAddImage
            )
        }
    }
}

@Composable
private fun ImageThumbnail(
    uri: Uri,
    onRemove: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(80.dp)
            .padding(4.dp)
    ) {
        AsyncImage(
            model = uri,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
        )
        IconButton(
            onClick = onRemove,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 8.dp, y = (-8).dp)
                .size(24.dp)
                .background(Color.Red, CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
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
            onBack = {}
        )
    }
}
