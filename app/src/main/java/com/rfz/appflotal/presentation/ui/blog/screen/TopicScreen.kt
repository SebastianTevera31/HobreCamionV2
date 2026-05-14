package com.rfz.appflotal.presentation.ui.blog.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.rfz.appflotal.presentation.theme.Dimens
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.blog.components.CommentCard
import com.rfz.appflotal.presentation.ui.blog.components.TopicHeader
import com.rfz.appflotal.presentation.ui.blog.viewmodel.Comment
import com.rfz.appflotal.presentation.ui.blog.viewmodel.Post

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TopicScreen(
    topic: Post,
    mainComment: Comment,
    comments: List<Comment>,
    firstInitial: String,
    isSaved: Boolean,
    secondInitial: String,
    onReply: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        LazyColumn(
            contentPadding = PaddingValues(Dimens.PaddingSmall),
            verticalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall),
            modifier = Modifier.fillMaxSize()
        ) {
            stickyHeader {
                Column {
                    TopicHeader(
                        title = topic.title,
                        content = topic.description
                    )

                    CommentCard(
                        firstInitial = firstInitial,
                        user = mainComment.title,
                        content = mainComment.description,
                        imageUrl = mainComment.imageUrl,
                        likes = mainComment.likes,
                        isSaved = isSaved,
                        onReply = onReply,
                        onSave = onSave,
                        secondInitial = secondInitial
                    )
                }
            }

            items(comments) { comment ->
                CommentCard(
                    firstInitial = "C",
                    user = comment.title,
                    content = comment.description,
                    imageUrl = comment.imageUrl,
                    likes = comment.likes,
                    modifier = modifier,
                    onReply = onReply,
                    onSave = onSave,
                    secondInitial = "C"
                )
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
        likes = 10
    )
    val sampleComments = listOf(
        Comment(
            id = 2,
            title = "Carlos Ruiz",
            description = "Yo siempre mantengo la presión de los neumáticos al nivel recomendado.",
            imageUrl = "",
            likes = 5
        ),
        Comment(
            id = 3,
            title = "Ana Martínez",
            description = "Evitar frenazos bruscos también ayuda mucho.",
            imageUrl = "",
            likes = 8
        )
    )

    HombreCamionTheme {
        TopicScreen(
            topic = sampleTopic,
            mainComment = sampleMainComment,
            comments = sampleComments,
            firstInitial = "J",
            isSaved = false,
            secondInitial = "A",
            onReply = {},
            onSave = {}
        )
    }
}


