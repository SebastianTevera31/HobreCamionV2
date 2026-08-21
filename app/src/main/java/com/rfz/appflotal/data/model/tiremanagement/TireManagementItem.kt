package com.rfz.appflotal.data.model.tiremanagement

data class TireManagementItem(
    val id: Int,
    val title: String,
    val description: String = "",
    val treathDepth: Int? = null,
    val brandId: String? = null,
    val usage: String? = null
)