package com.rfz.appflotal.data.model.disassembly.response

import com.google.gson.annotations.SerializedName

data class DisassemblyCauseResponse(
    @SerializedName("id_disassemblyCause")
    val idDisassemblyCause: Int,

    @SerializedName("fld_description")
    val description: String,

    @SerializedName("c_user_fk_1")
    val userId: Int,

    @SerializedName("fld_active")
    val isActive: Boolean
)
