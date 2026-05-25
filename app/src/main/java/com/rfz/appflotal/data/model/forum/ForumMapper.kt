package com.rfz.appflotal.data.model.forum

import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt
import com.rfz.appflotal.core.util.Commons
import com.rfz.appflotal.presentation.ui.forums.viewmodel.RecordType

fun ForumResult.toRoom(): ForumRoom {
    return ForumRoom(
        id = this.idForum,
        title = this.fldTitle,
        description = this.fldDescription,
        imageUrl = this.fldImage
    )
}

fun TopicResult.toTopic(): ForumTopic {
    return ForumTopic(
        id = this.idTopic,
        title = this.fldTitle,
        description = this.fldDescription,
        imageUrl = this.fldImage,
        author = this.fldUserName,
        numComments = this.fldMessages,
        idUser = this.idUser,
        time = Commons.getRelativeTime(this.fldRegistrationDate),
        color = runCatching {
            val colorStr = this.fldColor.trim()
            val finalColor = if (colorStr.startsWith("#") || colorStr.isEmpty()) colorStr
            else "#$colorStr"
            Color(finalColor.toColorInt())
        }.getOrDefault(Color.Transparent),
        likes = this.fldLike,
        isLiked = this.isLiked
    )
}

fun ForumTopic.toComment(): ForumComment {
    return ForumComment(
        id = this.id,
        title = this.title,
        description = this.description,
        imageUrl = this.imageUrl,
        time = this.time,
        likes = this.likes,
        firstInitial = "",
        secondInitial = "",
        isLiked = this.isLiked
    )
}

fun TopicMessageResult.toComment(): ForumComment {
    val (first, second) = Commons.getInitials(this.fldUserName)
    return ForumComment(
        id = this.idTopicMessages,
        title = this.fldUserName,
        description = this.fldMessage,
        imageUrl = this.fldImage,
        likes = this.fldLike,
        firstInitial = first,
        secondInitial = second,
        time = Commons.getRelativeTime(this.fldRegistrationDate),
        isLiked = this.isLiked
    )
}

fun LikedPostResult.toEntity(): LikedRecord? {
    val summarized = this.summarizedPublication.split(",")
    val complete = this.completePublication.split(",")

    if (summarized.size < 3 || complete.size < 2) return null

    return runCatching {
        // Usamos substringAfter(":") para ser más robustos si hay más de un ":"
        val type = summarized[0].substringAfter(":").trim()
        val username = summarized[1].substringAfter(":").trim()

        // Unimos de nuevo si el contenido tenía comas (índice 2 en adelante)
        val content = summarized.drop(2).joinToString(",").substringAfter(":").trim()

        val isTopic = type == "topic"
        val recordType = if (isTopic) RecordType.TOPIC else RecordType.COMMENT

        var topicId = 0
        var commentId = 0

        if (isTopic) {
            // "type: topic, id_topic: 2" -> id está en el índice 1
            topicId = complete[1].substringAfter(":").trim().toIntOrNull() ?: 0
        } else {
            // "type: message, id_message: 17, id_topic: 2"
            commentId = complete[1].substringAfter(":").trim().toIntOrNull() ?: 0
            if (complete.size >= 3) {
                topicId = complete[2].substringAfter(":").trim().toIntOrNull() ?: 0
            }
        }

        val (first, second) = Commons.getInitials(username)

        val datePart = this.likedDate.substringBefore(".")
        LikedRecord(
            likedId = this.idLiked,
            title = content,
            type = recordType,
            firstInitial = first,
            secondInitial = second,
            author = username,
            date = Commons.convertDate(datePart),
            topicId = topicId,
            commentId = commentId
        )
    }.getOrNull()
}