package com.rfz.appflotal.data.model.forum

import androidx.compose.ui.graphics.Color
import com.rfz.appflotal.data.model.CatalogItem
import com.rfz.appflotal.presentation.ui.forums.viewmodel.RecordType


data class ForumComment(
    override val id: Int,
    override val title: String,
    override val description: String,
    override val imageUrl: String,
    val time: String,
    val idUser: Int,
    val likes: Int,
    val firstInitial: String,
    val secondInitial: String,
    val isLiked: Boolean
) : ForumRecord

data class ForumTopic(
    override val id: Int,
    override val title: String,
    override val description: String,
    override val imageUrl: String,
    val author: String,
    val numComments: Int,
    val time: String,
    val idUser: Int,
    val color: Color,
    val likes: Int,
    val isLiked: Boolean
) : ForumRecord

data class ForumRoom(
    override val id: Int,
    override val title: String,
    override val description: String,
    override val imageUrl: String
) : ForumRecord

data class LikedRecord(
    val likedId: Int,
    val title: String,
    val topicId: Int = 0,
    val commentId: Int = 0,
    val author: String,
    val type: RecordType,
    val date: String,
    val firstInitial: String,
    val secondInitial: String
)

interface ForumRecord : CatalogItem {
    override val id: Int
    val title: String
    override val description: String
    val imageUrl: String
}
