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
import com.rfz.appflotal.presentation.theme.Dimens
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.forums.components.ForumShimmerList
import com.rfz.appflotal.presentation.ui.forums.viewmodel.Post
import com.rfz.appflotal.presentation.ui.forums.viewmodel.PostType
import com.rfz.appflotal.presentation.ui.utils.LoadState

@Composable
fun PostsScreen(
    posts: List<Post>,
    modifier: Modifier = Modifier,
    loadState: LoadState<Any> = LoadState.Idle,
    onPostClick: (Post) -> Unit,
    onReport: (postId: Int, type: PostType) -> Unit
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
                items(posts) { post ->
                    PostCard(
                        title = post.title,
                        content = post.description,
                        modifier = Modifier.padding(Dimens.PaddingSmall),
                        numComments = post.numComments,
                        isPost = true,
                        showOptions = true,
                        onNav = { onPostClick(post) },
                        author = post.author,
                        time = post.time,
                        firstInitial = post.author.take(1).uppercase(),
                        secondInitial = "",
                        onReport = { onReport(post.id, PostType.TOPIC) }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PostsScreenPreview() {
    val samplePosts = listOf(
        Post(
            id = 1,
            title = "Noticia 1",
            description = "Descripción de la noticia 1",
            imageUrl = "",
            author = "Autor 1",
            numComments = 5,
            time = "hace 2 horas"
        ),
        Post(
            id = 2,
            title = "Noticia 2",
            description = "Descripción de la noticia 2",
            imageUrl = "",
            author = "Autor 2",
            numComments = 10,
            time = "hace 5 horas"
        ),
        Post(
            id = 3,
            title = "Noticia 3",
            description = "Descripción de la noticia 3",
            imageUrl = "",
            author = "Autor 3",
            numComments = 0,
            time = "hace 1 día"
        )
    )
    HombreCamionTheme {
        PostsScreen(posts = samplePosts, onPostClick = {}, onReport = {_, _ ->})
    }
}
