package com.rfz.appflotal.presentation.ui.blog.screen

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
import com.rfz.appflotal.presentation.ui.blog.components.PostCard
import com.rfz.appflotal.presentation.ui.blog.viewmodel.Post

@Composable
fun PostsScreen(comments: List<Post>, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .fillMaxSize(),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        LazyColumn(
            contentPadding = PaddingValues(Dimens.PaddingSmall),
            modifier = Modifier.fillMaxSize()
        ) {
            items(comments) { comment ->
                PostCard(
                    title = comment.title,
                    content = comment.description,
                    modifier = Modifier.padding(Dimens.PaddingSmall),
                    numComments = comment.numComments,
                    isPost = true,
                    onClick = {},
                    author = comment.author,
                    time = comment.time,
                    firstInitial = "F",
                    secondInitial = "S"
                )
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
        PostsScreen(comments = samplePosts)
    }
}
