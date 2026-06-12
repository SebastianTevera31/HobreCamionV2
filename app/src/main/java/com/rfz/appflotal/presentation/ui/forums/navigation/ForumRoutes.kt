package com.rfz.appflotal.presentation.ui.forums.navigation

import kotlinx.serialization.Serializable

@Serializable
object ForumsGraph

@Serializable
object ForumRooms

@Serializable
data class RoomTopics(
    val roomId: String,
    val roomTitle: String,
)

@Serializable
data class TopicDiscussion(
    val roomId: String,
    val topicId: String,
    val topicTitle: String,
    val selectedComment: String = "0"
)

@Serializable
data class ReportContent(
    val id: Int,
    val isComment: Boolean
)

@Serializable
data class NewTopicNav(
    val roomId: String,
    val roomTitle: String
)

@Serializable
data class NewCommentNav(
    val id: Int,
    val isTopic: Boolean = false
)

@Serializable
object SavedCommentsNav
