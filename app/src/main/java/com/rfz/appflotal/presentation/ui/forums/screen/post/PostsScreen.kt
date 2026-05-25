package com.rfz.appflotal.presentation.ui.forums.screen.post

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
import com.rfz.appflotal.data.model.forum.ForumTopic
import com.rfz.appflotal.presentation.theme.Dimens
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.forums.components.ForumShimmerList
import com.rfz.appflotal.presentation.ui.forums.viewmodel.RecordType
import com.rfz.appflotal.presentation.ui.utils.LoadState

@Composable
fun TopicsScreen(
    topics: List<ForumTopic>,
    modifier: Modifier = Modifier,
    loadState: LoadState<Any> = LoadState.Idle,
    onTopicClick: (ForumTopic) -> Unit,
    onReport: (topicId: Int, type: RecordType) -> Unit
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        if (loadState is LoadState.Loading) {
            ForumShimmerList()
        } else {
            LazyColumn(
                contentPadding = PaddingValues(Dimens.PaddingSmall),
                modifier = Modifier.fillMaxSize()
            ) {
                items(topics) { topic ->
                    ForumCard(
                        title = topic.title,
                        content = topic.description,
                        imageUrl = topic.imageUrl,
                        modifier = Modifier.padding(Dimens.PaddingSmall),
                        numComments = topic.numComments,
                        isTopic = true,
                        showOptions = true,
                        onNav = { onTopicClick(topic) },
                        author = topic.author,
                        time = topic.time,
                        firstInitial = topic.author.take(1).uppercase(),
                        secondInitial = "",
                        onReport = { onReport(topic.id, RecordType.TOPIC) },
                        color = topic.color
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TopicsScreenPreview() {
    val sampleTopics = listOf(
        ForumTopic(
            id = 1,
            title = "Noticia 1",
            description = "Descripción de la noticia 1",
            imageUrl = "",
            author = "Autor 1",
            numComments = 5,
            time = "hace 2 horas",
            idUser = 1,
            color = MaterialTheme.colorScheme.primary,
            isSaved = false
        ),
        ForumTopic(
            id = 2,
            title = "Noticia 2",
            description = "Descripción de la noticia 2",
            imageUrl = "",
            author = "Autor 2",
            numComments = 10,
            time = "hace 5 horas",
            idUser = 2,
            color = MaterialTheme.colorScheme.primary,
            isSaved = false
        ),
        ForumTopic(
            id = 3,
            title = "Noticia 3",
            description = "Descripción de la noticia 3",
            imageUrl = "",
            author = "Autor 3",
            numComments = 0,
            time = "hace 1 día",
            idUser = 3,
            color = MaterialTheme.colorScheme.primary,
            isSaved = false
        )
    )
    HombreCamionTheme {
        TopicsScreen(topics = sampleTopics, onTopicClick = {}, onReport = { _, _ -> })
    }
}
