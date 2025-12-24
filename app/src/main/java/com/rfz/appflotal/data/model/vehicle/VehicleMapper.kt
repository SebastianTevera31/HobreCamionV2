package com.rfz.appflotal.data.model.vehicle

fun UpdateVehicle.toDto(): UpdateVehicleDto =
    UpdateVehicleDto(
        idVehicle = vehicleId,
        typeVehicle = typeVehicle,
        spareTires = spareTires,
        vehicleNumber = vehicleNumber,
        plates = plates,
        dailyMaximumDistance = dailyMaximumDistance,
        averageDailyDistance = averageDailyDistances
    )