package com.rfz.appflotal.data.model.utilization.response

import com.rfz.appflotal.data.model.utilization.UtilizationItem

data class UtilizationResponse(
    val id_utilization: Int,
    val fld_description: String
) {
    fun toDomain(): UtilizationItem {
        return UtilizationItem(
            id = id_utilization,
            description = fld_description
        )
    }
}