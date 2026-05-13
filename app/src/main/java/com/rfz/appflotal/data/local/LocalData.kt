package com.rfz.appflotal.data.local

import com.rfz.appflotal.data.model.CatalogItem

data class Catalog(
    override val id: Int,
    override val description: String,
    val enDescription: String
) : CatalogItem

val mapCountries = listOf(
    Catalog(
        id = 59,
        description = "Estados Unidos",
        enDescription = "United States"
    ),
    Catalog(
        id = 118,
        description = "México",
        enDescription = "Mexico"
    ),
    Catalog(
        id = 35,
        description = "Canadá",
        enDescription = "Canada"
    )
)