package com.rfz.appflotal.data.model.database

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(
    tableName = "forum_topic"
)
data class TopicEntity(
    @PrimaryKey val idTopic: Int,
    val idRoom: Int,
    val idUser: Int,
    val username: String,
    val image: String,
    val likes: Int,
    val color: String,
    val numMessages: Int,
    val tags: String,
    val date: String,
    val edited: Boolean
)

@Entity(tableName = "forum_comment")
data class CommentEntity(
    @PrimaryKey val idMsg: Int,
    val idTopic: Int,
    val idUser: Int,
    val username: String,
    val messages: String, // El contenido del comentario
    val likes: Int,
    val date: String,
    val edited: Boolean
)
