package com.rfz.appflotal.data.model.catalogo

import com.rfz.appflotal.data.model.catalog.GetTireInspectionReportResponse
import com.rfz.appflotal.data.model.destination.response.DestinationResponse
import com.rfz.appflotal.data.model.disassembly.response.DisassemblyCauseResponse
import com.rfz.appflotal.data.model.tire.response.TireListResponse

fun TireListResponse.aEntidad() = LlantaEntity(
    idLlanta = idTire,
    proveedor = provider.orEmpty(),
    medida = size,
    marca = brand,
    modelo = model,
    capacidadCarga = loadingCapacity,
    destino = destination,
    tipoAdquisicion = typeAcquisition,
    ultimaPosicionMontada = lastMountedPosition.orEmpty(),
    ultimoDisenoRenovado = descriptionLastRenovatedDesign.orEmpty(),
    fechaUltimaPosicionMontada = lastMountedPositionDate.orEmpty(),
    numeroVehiculo = vehicleNumber.orEmpty(),
    fechaEventoMontaje = dateEventAssembly.orEmpty(),
    fechaEventoA = dateEventA.orEmpty(),
    profundidadMontaje = treadDepthAssembly,
    odometroMontaje = odometerAssembly,
    tipoVehiculo = typeVehicle.orEmpty(),
)

fun LlantaEntity.aRespuesta() = TireListResponse(
    idTire = idLlanta,
    provider = proveedor,
    size = medida,
    brand = marca,
    model = modelo,
    loadingCapacity = capacidadCarga,
    destination = destino,
    typeAcquisition = tipoAdquisicion,
    lastMountedPosition = ultimaPosicionMontada,
    descriptionLastRenovatedDesign = ultimoDisenoRenovado,
    lastMountedPositionDate = fechaUltimaPosicionMontada,
    vehicleNumber = numeroVehiculo,
    dateEventAssembly = fechaEventoMontaje,
    dateEventA = fechaEventoA,
    treadDepthAssembly = profundidadMontaje,
    odometerAssembly = odometroMontaje,
    typeVehicle = tipoVehiculo,
)

fun DestinationResponse.aCatalogo() = CatalogoEntity(
    tipo = TipoDatoOffline.DESTINO,
    id = idDestination,
    descripcion = fldDescription,
    descripcionEn = fldEsDescription,
)

fun CatalogoEntity.aDestinoRespuesta() = DestinationResponse(
    idDestination = id,
    fldDescription = descripcion,
    fldEsDescription = descripcionEn ?: descripcion,
)

fun DisassemblyCauseResponse.aCatalogo() = CatalogoEntity(
    tipo = TipoDatoOffline.CAUSA_DESMONTAJE,
    id = idDisassemblyCause,
    descripcion = description,
    activo = isActive,
)

// El usuario dueño de la causa no se guarda: la tabla se borra al cerrar sesión.
fun CatalogoEntity.aCausaRespuesta() = DisassemblyCauseResponse(
    idDisassemblyCause = id,
    description = descripcion,
    userId = 0,
    isActive = activo,
)

fun GetTireInspectionReportResponse.aCatalogo() = CatalogoEntity(
    tipo = TipoDatoOffline.REPORTE_INSPECCION,
    id = idTireInspectionReport,
    descripcion = description,
)

fun CatalogoEntity.aReporteRespuesta() = GetTireInspectionReportResponse(
    idTireInspectionReport = id,
    description = descripcion,
)
