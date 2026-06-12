package com.rfz.appflotal.presentation.ui.forums.screen.topic

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.rfz.appflotal.R
import com.rfz.appflotal.data.model.forum.ForumComment
import com.rfz.appflotal.presentation.theme.Dimens
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.forums.components.CommentCard
import com.rfz.appflotal.presentation.ui.utils.LoadState

@Composable
fun ReplyScreen(
    comment: ForumComment,
    modifier: Modifier = Modifier,
    replyStatus: LoadState<Unit>,
    onBack: () -> Unit,
    selectedImage: Uri? = null,
    onAddImage: () -> Unit = {},
    onRemoveImage: () -> Unit = {},
    message: String = "",
    onMessageChange: (String) -> Unit = {}
) {
    val isLoading = replyStatus is LoadState.Loading

    BackHandler {
        onRemoveImage()
        onBack()
    }

    LaunchedEffect(replyStatus) {
        if (replyStatus is LoadState.Success) {
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
            if (replyStatus is LoadState.Error) {
                Text(
                    text = replyStatus.message,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            CommentCard(
                firstInitial = comment.firstInitial,
                user = comment.title,
                content = comment.description,
                imageUrl = comment.imageUrl,
                likes = comment.likes,
                isSaved = comment.isLiked,
                onReply = {},
                onSave = {},
                onSeeMore = {},
                showOptions = false,
                secondInitial = comment.secondInitial,
                time = comment.time
            )

            if (selectedImage != null) {
                ImageThumbnail(
                    uri = selectedImage,
                    onRemove = onRemoveImage
                )
            }

            ReplyEditor(
                message = message,
                isLoading = isLoading,
                onMessageChange = onMessageChange,
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

@Composable
private fun ReplyEditor(
    message: String,
    isLoading: Boolean,
    onMessageChange: (String) -> Unit,
    onAddImage: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement =
            Arrangement.spacedBy(Dimens.PaddingSmall),
        horizontalAlignment = Alignment.Start
    ) {
        IconButton(
            onClick = onAddImage,
            enabled = !isLoading,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = Icons.Default.AddAPhoto,
                contentDescription = "Agregar imagen",
                tint = MaterialTheme.colorScheme.primary
            )
        }

        OutlinedTextField(
            value = message,
            onValueChange = onMessageChange,
            enabled = !isLoading,
            shape = RoundedCornerShape(Dimens.PaddingSmall),
            placeholder = {
                Text(
                    text = stringResource(
                        R.string.forum_comment_placeholder
                    )
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor =
                    MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor =
                    MaterialTheme.colorScheme.surfaceVariant,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent
            ),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 150.dp)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ReplyScreenPreview() {
    val sampleComment = ForumComment(
        id = 1,
        title = "Juan Pérez",
        description = "Este es un comentario de prueba para la pantalla de nuevo comentario.",
        imageUrl = "",
        likes = 0,
        firstInitial = "J",
        secondInitial = "P",
        isLiked = false,
        time = "Hace 2 dias",
        idUser = 2
    )
    HombreCamionTheme {
        ReplyScreen(
            comment = sampleComment,
            replyStatus = LoadState.Idle,
            modifier = Modifier.safeContentPadding(),
            onBack = {},
            message = "Comentario de prueba",
            onMessageChange = {}
        )
    }
}
