package com.rfz.appflotal.data.model.axle

import com.rfz.appflotal.domain.CatalogItem

fun AxleEntity.toDomain(): Axle {
    return Axle(
        id = idAxle,
        description = fldDescription,
    )
}

fun GetAxleResponseDto.toEntity(): AxleEntity {
    return AxleEntity(
        idAxle = idAxle,
        fldDescription = fldDescription,
        fldLetter = fldLetter
    )
}

fun Axle.toCatalogItem(): CatalogItem {
    return CatalogItem(
        id = id,
        description = description
    )
}