package com.rfz.appflotal.data.model.utilization.response

import com.rfz.appflotal.domain.CatalogItem

data class UtilizationResponse(
    val id_utilization: Int,
    val fld_description: String
) {
    fun toDomain(): CatalogItem {
        return CatalogItem(
            id = id_utilization,
            description = fld_description
        )
    }
}