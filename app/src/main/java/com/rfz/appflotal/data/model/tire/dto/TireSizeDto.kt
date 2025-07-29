package com.rfz.appflotal.data.model.tire.dto

import com.google.gson.annotations.SerializedName

data class TireSizeDto
(

    @SerializedName("id_tireSize")
    val id_tireSize: Int,

    @SerializedName("fld_size")
val fld_size: String,


@SerializedName("fld_notes")
val fld_notes: String,

@SerializedName("c_user_fk_1")
val c_user_fk_1: Int,
)