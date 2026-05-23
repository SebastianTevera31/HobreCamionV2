package com.rfz.appflotal.data.mapper

import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt
import com.rfz.appflotal.core.util.Commons
import com.rfz.appflotal.data.model.forum.ForumResult
import com.rfz.appflotal.data.model.forum.TopicMessageResult
import com.rfz.appflotal.data.model.forum.TopicResult
import com.rfz.appflotal.presentation.ui.forums.viewmodel.Comment
import com.rfz.appflotal.presentation.ui.forums.viewmodel.Post
import com.rfz.appflotal.presentation.ui.forums.viewmodel.Topic

fun ForumResult.toTopic(): Topic {
    return Topic(
        id = this.idForum,
        title = this.fldTitle,
        description = this.fldDescription,
        imageUrl = this.fldImage
    )
}

fun TopicResult.toPost(): Post {
    return Post(
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

fun Post.toComment(): Comment {
    return Comment(
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

fun TopicMessageResult.toComment(): Comment {
    return Comment(
        id = this.idTopicMessages,
        title = this.fldUserName,
        description = this.fldMessage,
        imageUrl = this.fldImage,
        likes = this.fldLike,
        isSaved = false,
        firstInitial = this.fldUserName.take(1).uppercase(),
        secondInitial = "",
        time = Commons.getRelativeTime(this.fldRegistrationDate)
    )
}
