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
        isSaved = false,
        time = Commons.getRelativeTime(this.fldRegistrationDate),
        color = runCatching {
            val colorStr = this.fldColor.trim()
            val finalColor = if (colorStr.startsWith("#") || colorStr.isEmpty()) colorStr
            else "#$colorStr"
            Color(finalColor.toColorInt())
        }.getOrDefault(Color.Transparent)
    )
}

fun ForumTopic.toComment(): ForumComment {
    return ForumComment(
        id = this.id,
        title = this.title,
        description = this.description,
        imageUrl = this.imageUrl,
        time = this.time,
        likes = 0,
        isSaved = false,
        firstInitial = "",
        secondInitial = ""
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
        isSaved = false,
        firstInitial = first,
        secondInitial = second,
        time = Commons.getRelativeTime(this.fldRegistrationDate)
    )
}

fun LikedPostResult.toEntity(): LikedRecord? {
    val completeData = this.summarizedPublication.split(",")
    if (completeData.size < 7) return null
    val type = completeData[0].split(":").firstOrNull() ?: return null
    val id = completeData[1].split(":").firstOrNull()?.toIntOrNull() ?: return null
    val username = completeData[2].split(":").firstOrNull() ?: return null
    val title = completeData[3].split(":").firstOrNull() ?: return null
    val description = completeData[4].split(":").firstOrNull() ?: return null
    val image = completeData[5].split(":").firstOrNull() ?: return null
    val likes = completeData[6].split(":").firstOrNull()?.toIntOrNull() ?: return null

    val (first, second) = Commons.getInitials(username)

    val recordType = if (type == "topic") RecordType.TOPIC else RecordType.COMMENT

    return LikedRecord(
        likedId = this.idLiked,
        id = id,
        title = title,
        description = description,
        imageUrl = image,
        likes = likes,
        type = recordType,
        firstInitial = first,
        secondInitial = second,
        author = username,
        date = Commons.convertDate(this.likedDate)
    )
}