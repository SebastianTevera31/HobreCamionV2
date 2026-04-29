package com.rfz.appflotal.data.network.client.vialstatus

import com.google.gson.annotations.SerializedName

data class RoadMapDto(
    @SerializedName("id") val id: Int,
    @SerializedName("fld_link") val link: String
)