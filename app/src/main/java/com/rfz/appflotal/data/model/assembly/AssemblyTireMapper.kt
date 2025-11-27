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
        idMonitor = idMonitor,
        positionTire = positionTire,
        odometer = odometer,
        assemblyDate = assemblyDate
    )
}

fun AssemblyTireEntity.toDomain(): AssemblyTire {
    return AssemblyTire(
        idAxle = idAxle,
        idTire = idTire,
        positionTire = positionTire,
        odometer = odometer,
        assemblyDate = assemblyDate,
        updatedAt = updatedAt
    )
}

fun AssemblyTireResponse.toEntity(): AssemblyTireEntity {
    return AssemblyTireEntity(
        idAxle = axleId,
        idTire = tireId,
        idMonitor = 0,
        positionTire = positionTire,
        odometer = odometer,
        assemblyDate = assemblyDate,
        updatedAt = System.currentTimeMillis()
    )
}