package com.rfz.appflotal.data.model.forum

import com.google.gson.annotations.SerializedName

data class GetForumsResponse(
    @SerializedName("results") val results: List<ForumResult>,
    @SerializedName("total") val total: Int
)

data class ForumResult(
    @SerializedName("id_forum") val idForum: Int,
    @SerializedName("fld_title") val fldTitle: String,
    @SerializedName("fld_description") val fldDescription: String,
    @SerializedName("fld_registrationDate") val fldRegistrationDate: String,
    @SerializedName("fld_image") val fldImage: String,
    @SerializedName("fld_color") val fldColor: String
)

data class GetTopicsResponse(
    @SerializedName("results") val results: List<TopicResult>,
    @SerializedName("total") val total: Int
)

data class TopicResult(
    @SerializedName("id_topic") val idTopic: Int,
    @SerializedName("fld_title") val fldTitle: String,
    @SerializedName("fld_description") val fldDescription: String,
    @SerializedName("fld_color") val fldColor: String,
    @SerializedName("fld_like") val fldLike: Int,
    @SerializedName("fld_messages") val fldMessages: Int,
    @SerializedName("fld_image") val fldImage: String,
    @SerializedName("id_forum") val idForum: Int,
    @SerializedName("fld_tags") val fldTags: String,
    @SerializedName("fld_registrationDate") val fldRegistrationDate: String,
    @SerializedName("id_user") val idUser: Int,
    @SerializedName("fld_userName") val fldUserName: String,
    @SerializedName("fld_edited") val fldEdited: Boolean
)

data class TopicMessageResult(
    @SerializedName("id_topicMessages") val idTopicMessages: Int,
    @SerializedName("fld_message") val fldMessage: String,
    @SerializedName("fld_registrationDate") val fldRegistrationDate: String,
    @SerializedName("id_user") val idUser: Int,
    @SerializedName("fld_userName") val fldUserName: String,
    @SerializedName("fld_like") val fldLike: Int,
    @SerializedName("id_topic") val idTopic: Int,
    @SerializedName("fld_edited") val fldEdited: Boolean,
    @SerializedName("fld_image") val fldImage: String
)

data class CrudTopicMessageRequest(
    @SerializedName("id_topicMessage") val idTopicMessage: Int,
    @SerializedName("message") val message: String,
    @SerializedName("registrationDate") val registrationDate: String,
    @SerializedName("id_topic") val idTopic: Int,
    @SerializedName("image") val image: String
)
