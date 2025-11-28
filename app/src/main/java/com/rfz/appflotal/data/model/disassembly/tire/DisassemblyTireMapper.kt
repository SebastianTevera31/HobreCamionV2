package com.rfz.appflotal.data.model.disassembly.tire

fun DisassemblyTire.toDto(): DisassemblyTireRequestDto {
    return DisassemblyTireRequestDto(
        disassemblyCause = disassemblyCause,
        destination = destination,
        dateOperation = dateOperation,
        positionTire = positionTire,
        odometer = odometer
    )
}