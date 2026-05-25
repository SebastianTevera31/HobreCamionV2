package com.rfz.appflotal.presentation.ui.forums.screen.topic

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.rfz.appflotal.data.model.forum.ForumComment
import com.rfz.appflotal.data.model.forum.ForumTopic
import com.rfz.appflotal.presentation.theme.Dimens
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.forums.components.CommentCard
import com.rfz.appflotal.presentation.ui.forums.viewmodel.RecordType

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DiscussionScreen(
    topic: ForumTopic,
    comments: List<ForumComment>,
    onReply: (ForumComment) -> Unit,
    onSave: (id: Int, isComment: Boolean) -> Unit,
    onReport: (id: Int, type: RecordType) -> Unit,
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
                        onReport = { onReport(topic.id, RecordType.TOPIC) },
                        onSave = { onSave(topic.id, false) },
                        isSaved = topic.isLiked,
                        likes = 3,
                        time = topic.time,
                        color = topic.color
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
                        onSave = { onSave(comment.id, true) },
                        isAuthor = comment.id == topic.idUser,
                        isSaved = comment.isLiked,
                        onReport = { onReport(comment.id, RecordType.COMMENT) },
                        secondInitial = comment.secondInitial,
                        time = comment.time
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DiscussionScreenPreview() {
    val sampleTopic = ForumTopic(
        id = 1,
        title = "Cómo mejorar la eficiencia de combustible",
        description = "Compartamos consejos sobre cómo ahorrar combustible en rutas largas.",
        imageUrl = "",
        author = "Juan Pérez",
        numComments = 15,
        time = "hace 3 horas",
        idUser = 1,
        color = MaterialTheme.colorScheme.primary,
        likes = 0,
        isLiked = true
    )

    val sampleComments = listOf(
        ForumComment(
            id = 2,
            title = "Admin",
            description = "Excelente tema para discutir. Aquí les dejo mis primeros consejos...",
            imageUrl = "",
            likes = 10,
            firstInitial = "A",
            secondInitial = "",
            time = "",
            isLiked = false
        ),
        ForumComment(
            id = 1,
            title = "Carlos Ruiz",
            description = "Yo siempre mantengo la presión de los neumáticos al nivel recomendado.",
            imageUrl = "",
            likes = 5,
            firstInitial = "C",
            secondInitial = "R",
            time = "",
            isLiked = true
        ),
        ForumComment(
            id = 3,
            title = "Ana Martínez",
            description = "Evitar frenazos bruscos también ayuda mucho.",
            imageUrl = "",
            likes = 8,
            firstInitial = "A",
            secondInitial = "M",
            time = "",
            isLiked = false
        )
    )

    HombreCamionTheme {
        DiscussionScreen(
            topic = sampleTopic,
            comments = sampleComments,
            onReply = {},
            onSave = { _, _ -> },
            onReport = { _, _ -> }
        )
    }
}
