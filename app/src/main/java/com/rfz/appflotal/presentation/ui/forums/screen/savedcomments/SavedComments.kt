package com.rfz.appflotal.presentation.ui.forums.screen.savedcomments

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.rfz.appflotal.data.model.forum.LikedRecord
import com.rfz.appflotal.presentation.commons.ErrorView
import com.rfz.appflotal.presentation.commons.LoadingIndicator
import com.rfz.appflotal.presentation.theme.Dimens
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.forums.components.RecordCard
import com.rfz.appflotal.presentation.ui.forums.viewmodel.RecordType
import com.rfz.appflotal.presentation.ui.utils.LoadState

@Composable
fun SavedCommentsRoute(
    modifier: Modifier = Modifier,
    onNavigateTo: (idRecord: Int, title: String, isComment: Boolean) -> Unit,
    viewModel: SavedCommentsViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadComments()
    }

    when (uiState.value.savedCommentState) {
        is LoadState.Error -> {
            ErrorView(showRetryButton = true) {
                viewModel.loadComments()
            }
        }

        LoadState.Loading -> {
            LoadingIndicator()
        }

        is LoadState.Success -> {
            SavedCommentsScreen(
                likedComments = uiState.value.comments,
                modifier = modifier,
                onNavigateTo = onNavigateTo,
                onDeleteLike = viewModel::deleteComment
            )
        }

        else -> {}
    }
}

@Composable
fun SavedCommentsScreen(
    likedComments: List<LikedRecord>,
    modifier: Modifier = Modifier,
    onDeleteLike: (idRecord: Int, type: RecordType) -> Unit,
    onNavigateTo: (idRecord: Int, title: String, isComment: Boolean) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(Dimens.PaddingSmall),
        verticalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall),
        modifier = Modifier.fillMaxSize()
    ) {
        items(likedComments, key = { it.likedId }) { record ->
            RecordCard(
                header = if (record.type.isComment) record.description else record.title,
                time = record.date,
                imageUrl = record.imageUrl,
                likes = record.likes,
                isComment = record.type.isComment,
                isSaved = true,
                firstInitial = record.firstInitial,
                secondInitial = record.secondInitial,
                onSave = { onDeleteLike(record.likedId, record.type) },
                onSeeMore = { onNavigateTo(record.likedId, record.title, record.type.isComment) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SavedCommentsScreenPreview() {
    val sampleComments = listOf(
        LikedRecord(
            id = 1,
            title = "Topic Title 1",
            description = "This is a comment description for the first topic.",
            imageUrl = "",
            likedId = 101,
            likes = 10,
            author = "Author One",
            type = RecordType.COMMENT,
            date = "2 hours ago",
            firstInitial = "A",
            secondInitial = "O"
        ),
        LikedRecord(
            id = 2,
            title = "Topic Title 2",
            description = "This is another comment description for the second topic.",
            imageUrl = "",
            likedId = 102,
            likes = 5,
            author = "Author Two",
            type = RecordType.TOPIC,
            date = "5 hours ago",
            firstInitial = "B",
            secondInitial = "T"
        )
    )
    HombreCamionTheme {
        SavedCommentsScreen(
            likedComments = sampleComments,
            onDeleteLike = { _, _ -> },
            onNavigateTo = { _, _, _ -> }
        )
    }
}