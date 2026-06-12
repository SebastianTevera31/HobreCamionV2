package com.rfz.appflotal.presentation.ui.forums.viewmodel

import com.rfz.appflotal.data.model.forum.ForumComment
import com.rfz.appflotal.data.model.forum.ForumRoom
import com.rfz.appflotal.data.model.forum.ForumTopic
import com.rfz.appflotal.presentation.ui.utils.LoadState

data class ForumUiState(
    val screenState: LoadState<Unit> = LoadState.Idle,
    val newTopicState: LoadState<Unit> = LoadState.Idle,
    val sendCommentState: LoadState<Unit> = LoadState.Idle,
    val roomState: LoadState<Unit> = LoadState.Idle,
    val reportState: LoadState<Unit> = LoadState.Idle,
    val rooms: List<ForumRoom> = emptyList(),
    val topics: List<ForumTopic> = emptyList(),
    val filteredTopics: List<ForumTopic> = emptyList(),
    val filteredRooms: List<ForumRoom> = emptyList(),
    val selectedTopic: ForumTopic? = null,
    val selectedRoom: ForumRoom? = null,
    val comments: List<ForumComment> = emptyList(),
    val searchQuery: String = "",
    val photoEvidence: CameraUiState = CameraUiState.Idle,
    val commentText: String = "",
    val shouldNavigateToReply: Boolean = false,
    val topicTitle: String = "",
    val topicDescription: String = "",
    val topicColor: String = "#F44336",
    val topicTags: List<String> = emptyList()
)

sealed class CameraUiState {
    object Idle : CameraUiState()
    object TakingPhoto : CameraUiState()
    data class Captured(val uri: android.net.Uri) : CameraUiState()
    data class Error(val message: String) : CameraUiState()
}

enum class RecordType(val isComment: Boolean) {
    TOPIC(false), COMMENT(true)
}

enum class ForumScreenType {
    ROOM, TOPIC
}