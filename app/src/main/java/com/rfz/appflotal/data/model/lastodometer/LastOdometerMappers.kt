package com.rfz.appflotal.data.model.lastodometer

fun LastOdometerResponseDto.toDomain(): LastOdometer {
    return LastOdometer(
        idVehicle = idVehicle,
        lastOdometer = lastOdometer,
        dateOdometer = dateOdometer
    )
}
