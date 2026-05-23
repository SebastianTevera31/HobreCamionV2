package com.rfz.appflotal.presentation.ui.forums.screen.topic

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
import com.rfz.appflotal.presentation.ui.forums.viewmodel.Comment
import com.rfz.appflotal.presentation.ui.forums.viewmodel.Post
import com.rfz.appflotal.presentation.ui.forums.viewmodel.PostType

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TopicScreen(
    topic: Post,
    comments: List<Comment>,
    onReply: (Comment) -> Unit,
    onSave: (id: Int, isPost: Boolean) -> Unit,
    onReport: (postId: Int, type: PostType) -> Unit,
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
            item {
                Column(modifier = Modifier.padding(Dimens.PaddingExtraSmall)) {
                    TopicHeader(
                        title = topic.title,
                        content = topic.description,
                        imageUrl = topic.imageUrl,
                        onReport = { onReport(topic.id, PostType.POST) },
                        onSave = { onSave(topic.id, true) },
                        isSaved = false,
                        likes = 3,
                        time = topic.time
                    )
                }
            }

            items(comments) { comment ->
                Column(modifier = Modifier.padding(horizontal = Dimens.PaddingExtraSmall)) {
                    CommentCard(
                        isPost = true,
                        firstInitial = comment.firstInitial,
                        user = comment.title,
                        content = comment.description,
                        imageUrl = comment.imageUrl,
                        likes = comment.likes,
                        onReply = { onReply(comment) },
                        onSave = { onSave(comment.id, false) },
                        isAuthor = comment.id == topic.idUser,
                        isSaved = comment.isSaved,
                        onReport = { onReport(comment.id, PostType.COMMENT) },
                        secondInitial = comment.secondInitial,
                        time = comment.time
                    )
                }
            }
        }


        Column(modifier = Modifier.padding(Dimens.PaddingMedium)) {


            Row(modifier = Modifier.weight(1f)) {
                Spacer(modifier = Modifier.padding(Dimens.PaddingMedium))

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
        time = "hace 3 horas",
        idUser = 1,
        color = MaterialTheme.colorScheme.primary,
        isSaved = false
    )

    val sampleComments = listOf(
        Comment(
            id = 2,
            title = "Admin",
            description = "Excelente tema para discutir. Aquí les dejo mis primeros consejos...",
            imageUrl = "",
            likes = 10,
            firstInitial = "A",
            secondInitial = "",
            isSaved = true,
            time = ""
        ),
        Comment(
            id = 1,
            title = "Carlos Ruiz",
            description = "Yo siempre mantengo la presión de los neumáticos al nivel recomendado.",
            imageUrl = "",
            likes = 5,
            firstInitial = "C",
            secondInitial = "R",
            isSaved = false,
            time = ""
        ),
        Comment(
            id = 3,
            title = "Ana Martínez",
            description = "Evitar frenazos bruscos también ayuda mucho.",
            imageUrl = "",
            likes = 8,
            firstInitial = "A",
            secondInitial = "M",
            isSaved = true,
            time = ""
        )
    )

    HombreCamionTheme {
        TopicScreen(
            topic = sampleTopic,
            comments = sampleComments,
            onReply = {},
            onSave = { _, _ -> },
            onReport = { _, _ -> }
        )
    }
}
