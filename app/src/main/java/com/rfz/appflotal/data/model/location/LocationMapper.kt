package com.rfz.appflotal.data.model.location

import com.rfz.appflotal.data.local.Catalog
import com.rfz.appflotal.data.model.catalog.StateDto

fun StateDto.toDomain() =
    Catalog(
        id = idState,
        description = stateName,
        enDescription = stateName
    )
