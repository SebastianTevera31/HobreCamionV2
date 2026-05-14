package com.rfz.appflotal.presentation.ui.blog.viewmodel

import com.rfz.appflotal.data.model.CatalogItem
import com.rfz.appflotal.presentation.ui.utils.LoadState

data class BlogUiState(
    val screenState: LoadState<Unit> = LoadState.Idle,
)

data class Comment(
    override val id: Int,
    override val title: String,
    override val description: String,
    override val imageUrl: String,
    val likes: Int
) : BlogRecord

data class Post(
    override val id: Int,
    override val title: String,
    override val description: String,
    override val imageUrl: String,
    val author: String,
    val numComments: Int,
    val time: String
) : BlogRecord

data class Topic(
    override val id: Int,
    override val title: String,
    override val description: String,
    override val imageUrl: String
) : BlogRecord

interface BlogRecord : CatalogItem {
    override val id: Int
    val title: String
    override val description: String
    val imageUrl: String
}