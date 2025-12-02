package com.rfz.appflotal.data.model.repair

fun RepairCauseDto.toDomain(): RepairCause {
    return RepairCause(
        id = id,
        description = description
    )
}