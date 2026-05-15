package com.rfz.appflotal.presentation.ui.forums.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.rfz.appflotal.presentation.theme.Dimens
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.forums.components.CommentCard
import com.rfz.appflotal.presentation.ui.forums.components.TopicHeader
import com.rfz.appflotal.presentation.ui.forums.viewmodel.Comment
import com.rfz.appflotal.presentation.ui.forums.viewmodel.Post

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TopicScreen(
    topic: Post,
    mainComment: Comment,
    comments: List<Comment>,
    onReply: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(modifier = Modifier.padding(Dimens.PaddingMedium)) {
            TopicHeader(
                title = topic.title,
                content = topic.description
            )
            Spacer(modifier = Modifier.padding(Dimens.PaddingExtraSmall))
            CommentCard(
                firstInitial = mainComment.firstInitial,
                user = mainComment.title,
                content = mainComment.description,
                imageUrl = mainComment.imageUrl,
                likes = mainComment.likes,
                isSaved = mainComment.isSaved,
                onReply = onReply,
                onSave = onSave,
                isAuthor = true,
                secondInitial = mainComment.secondInitial
            )

            Row {
                Spacer(modifier = Modifier.padding(Dimens.PaddingMedium))
                LazyColumn(
                    contentPadding = PaddingValues(Dimens.PaddingSmall),
                    verticalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(comments) { comment ->
                        CommentCard(
                            firstInitial = comment.firstInitial,
                            user = comment.title,
                            content = comment.description,
                            imageUrl = comment.imageUrl,
                            likes = comment.likes,
                            onReply = onReply,
                            onSave = onSave,
                            isSaved = comment.isSaved,
                            secondInitial = comment.secondInitial
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TopicScreenPreview() {
    val sampleTopic = Post(
        id = 1,
        title = "Cómo mejorar la eficiencia de combustible",
        description = "Compartamos consejos sobre cómo ahorrar combustible en rutas largas.",
        imageUrl = "",
        author = "Juan Pérez",
        numComments = 15,
        time = "hace 3 horas"
    )
    val sampleMainComment = Comment(
        id = 1,
        title = "Admin",
        description = "Excelente tema para discutir. Aquí les dejo mis primeros consejos...",
        imageUrl = "",
        likes = 10,
        firstInitial = "A",
        secondInitial = "",
        isSaved = true
    )
    val sampleComments = listOf(
        Comment(
            id = 2,
            title = "Carlos Ruiz",
            description = "Yo siempre mantengo la presión de los neumáticos al nivel recomendado.",
            imageUrl = "",
            likes = 5,
            firstInitial = "C",
            secondInitial = "R",
            isSaved = false
        ),
        Comment(
            id = 3,
            title = "Ana Martínez",
            description = "Evitar frenazos bruscos también ayuda mucho.",
            imageUrl = "",
            likes = 8,
            firstInitial = "A",
            secondInitial = "M",
            isSaved = true
        )
    )

    HombreCamionTheme {
        TopicScreen(
            topic = sampleTopic,
            mainComment = sampleMainComment,
            comments = sampleComments,
            onReply = {},
            onSave = {}
        )
    }
}
