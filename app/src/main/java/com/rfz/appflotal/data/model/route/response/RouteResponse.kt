package com.rfz.appflotal.data.model.route.response

import com.google.gson.annotations.SerializedName

data class RouteResponse (
    @SerializedName("id_route")
    val idRoute: Int,

    @SerializedName("fld_description")
    val description: String,
)