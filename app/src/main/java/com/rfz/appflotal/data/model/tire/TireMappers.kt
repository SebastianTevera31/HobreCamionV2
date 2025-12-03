package com.rfz.appflotal.data.model.tire

import com.rfz.appflotal.data.model.tire.response.TireListResponse

fun RepairedTire.toDto() = RepairedTireDto(
    idRepairedTire = id,
    tireId = tireId,
    cost = cost,
    repairId = repairId,
    dateOperation = dateOperation.toString()
)

fun RetreatedTire.toDto() = RetreatedTireDto(
    idRetreadedTire = id,
    tireId = tireId,
    cost = cost,
    dateOperation = dateOperation.toString(),
    retreadDesignId = retreadDesignId
)

fun TireListResponse.toTire(): Tire {
    return Tire(
        id = idTire,
        description = "$brand - size: $size",
        size = size,
        brand = brand,
        model = model,
        thread = treadDepthAssembly,
        loadingCapacity = loadingCapacity,
        destination = destination
    )
}

fun ChangeDestination.toDto() =
    ChangeDestinationDto(
        tireId = tireId,
        destinationId = destinationId,
        changeMotive = changeMotive,
        dateOperation = dateOperation.toString()
    )