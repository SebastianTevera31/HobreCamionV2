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