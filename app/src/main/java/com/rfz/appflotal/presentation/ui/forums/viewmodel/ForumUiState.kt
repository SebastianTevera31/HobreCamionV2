package com.rfz.appflotal.presentation.ui.forums.viewmodel

import com.rfz.appflotal.data.model.CatalogItem
import com.rfz.appflotal.presentation.ui.utils.LoadState

data class ForumUiState(
    val screenState: LoadState<Unit> = LoadState.Idle,
    val forums: List<Topic> = emptyList(),
    val posts: List<Post> = emptyList(),
    val selectedPost: Post? = null,
    val selectedRoom: Topic? = null,
    val comments: List<Comment> = emptyList(),
    val searchQuery: String = ""
)

enum class PostType(val typeId: Int) {
    COMMENT(0), TOPIC(1)
}

data class Comment(
    override val id: Int,
    override val title: String,
    override val description: String,
    override val imageUrl: String,
    val likes: Int,
    val isSaved: Boolean,
    val firstInitial: String,
    val secondInitial: String
) : ForumRecord

data class Post(
    override val id: Int,
    override val title: String,
    override val description: String,
    override val imageUrl: String,
    val author: String,
    val numComments: Int,
    val time: String
) : ForumRecord

data class Topic(
    override val id: Int,
    override val title: String,
    override val description: String,
    override val imageUrl: String
) : ForumRecord

interface ForumRecord : CatalogItem {
    override val id: Int
    val title: String
    override val description: String
    val imageUrl: String
}