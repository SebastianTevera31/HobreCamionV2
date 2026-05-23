package com.rfz.appflotal.presentation.ui.forums.screen.topic

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.R
import com.rfz.appflotal.presentation.theme.Dimens
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.forums.components.CommentCard
import com.rfz.appflotal.presentation.ui.forums.viewmodel.Comment
import com.rfz.appflotal.presentation.ui.utils.LoadState

@Composable
fun ReplyScreen(
    comment: Comment,
    modifier: Modifier = Modifier,
    replyStatus: LoadState<Unit>,
    onSend: (String) -> Unit,
    onCancel: () -> Unit = {},
    onBack: () -> Unit
) {
    var message by remember { mutableStateOf("") }
    val isLoading = replyStatus is LoadState.Loading

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
            CommentCard(
                firstInitial = comment.firstInitial,
                user = comment.title,
                content = comment.description,
                imageUrl = comment.imageUrl,
                likes = comment.likes,
                isSaved = comment.isSaved,
                onReply = {},
                onSave = {},
                onSeeMore = {},
                showOptions = false,
                secondInitial = comment.secondInitial,
                time = comment.time
            )

            OutlinedTextField(
                value = message,
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.primary
                ),
                onValueChange = { message = it },
                shape = RoundedCornerShape(Dimens.PaddingSmall),
                placeholder = {
                    Text(
                        text = stringResource(R.string.forum_comment_placeholder),
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                enabled = !isLoading,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.Gray.copy(alpha = 0.3f),
                    unfocusedContainerColor = Color.Gray.copy(alpha = 0.3f),
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            if (replyStatus is LoadState.Error) {
                Text(
                    text = replyStatus.message,
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
                    onClick = { onSend(message) },
                    enabled = message.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(Dimens.PaddingMedium)
                ) {
                    Text(stringResource(R.string.title_enviar))
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ReplyScreenPreview() {
    val sampleComment = Comment(
        id = 1,
        title = "Juan Pérez",
        description = "Este es un comentario de prueba para la pantalla de nuevo comentario.",
        imageUrl = "",
        likes = 0,
        firstInitial = "J",
        secondInitial = "P",
        isSaved = false,
        time = "Hace 2 dias",
    )
    HombreCamionTheme {
        ReplyScreen(
            comment = sampleComment,
            replyStatus = LoadState.Idle,
            modifier = Modifier.safeContentPadding(),
            onSend = {},
            onBack = {}
        )
    }
}
