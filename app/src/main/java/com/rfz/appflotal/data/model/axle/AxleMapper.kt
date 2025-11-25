package com.rfz.appflotal.data.model.axle

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