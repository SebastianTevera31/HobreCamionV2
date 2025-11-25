package com.rfz.appflotal.data.model.assembly

fun AssemblyTire.toEntity(): AssemblyTireEntity {
    return AssemblyTireEntity(
        idAxle = idAxle,
        idTire = idTire,
        idMonitor = 0,
        positionTire = positionTire,
        odometer = odometer,
        assemblyDate = assemblyDate,
        updatedAt = updatedAt
    )
}

fun AssemblyTireEntity.toDto(): AssemblyTireDto {
    return AssemblyTireDto(
        idAxle = idAxle,
        idTire = idTire,
        idMonitor = 0,
        positionTire = positionTire,
        odometer = odometer,
        assemblyDate = assemblyDate
    )
}