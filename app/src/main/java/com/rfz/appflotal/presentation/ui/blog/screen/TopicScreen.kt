package com.rfz.appflotal.presentation.ui.blog.screen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.rfz.appflotal.presentation.theme.Dimens
import com.rfz.appflotal.presentation.ui.blog.viewmodel.Comment
import com.rfz.appflotal.presentation.ui.blog.viewmodel.Post

@Composable
fun TopicScreen(topic: Post, mainComment: Comment, comments: List<Comment>, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        LazyColumn(
            contentPadding = PaddingValues(Dimens.PaddingSmall),
            modifier = Modifier.fillMaxSize()
        ) {
            stickyHeader {
            }
        }
    }
}


