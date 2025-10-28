package com.rfz.appflotal.data.model.utilization.response

import com.rfz.appflotal.domain.Catalog

data class UtilizationResponse(
    val id_utilization: Int,
    val fld_description: String
) {
    fun toDomain(): Catalog {
        return Catalog(
            id = id_utilization,
            description = fld_description
        )
    }
}