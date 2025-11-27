package com.rfz.appflotal.data.model.utilization

import com.rfz.appflotal.data.model.CatalogItem

data class UtilizationItem(
    override val id: Int, override val description: String
) : CatalogItem