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
    val likes: Int,
    var isSaved: Boolean,
    val firstInitial: String,
    val secondInitial: String
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
    val isSaved: Boolean
) : ForumRecord

data class ForumRoom(
    override val id: Int,
    override val title: String,
    override val description: String,
    override val imageUrl: String
) : ForumRecord

data class LikedRecord(
    override val id: Int,
    override val title: String,
    override val description: String,
    override val imageUrl: String,
    val likedId: Int,
    val likes: Int,
    val author: String,
    val type: RecordType,
    val date: String,
    val firstInitial: String,
    val secondInitial: String
) : ForumRecord

interface ForumRecord : CatalogItem {
    override val id: Int
    val title: String
    override val description: String
    val imageUrl: String
}
