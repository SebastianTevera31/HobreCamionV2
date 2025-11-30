package com.rfz.appflotal.data.model.waster

import com.rfz.appflotal.data.model.CatalogItem

data class WasteReport(
    override val id: Int,
    override val description: String,
    val type: String
) : CatalogItem