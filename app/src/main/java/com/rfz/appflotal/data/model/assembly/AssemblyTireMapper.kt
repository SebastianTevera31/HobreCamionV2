package com.rfz.appflotal.data.model.assembly

object AssemblyTireMapper {
    fun AssemblyTireDto.toEntity(): AssemblyTireEntity {
        return AssemblyTireEntity(
            idAxle = idAxle,
            idTire = idTire,
            idMonitor = idMonitor,
            positionTire = positionTire,
            odometer = odometer,
            assemblyDate = assemblyDate
        )
    }
}