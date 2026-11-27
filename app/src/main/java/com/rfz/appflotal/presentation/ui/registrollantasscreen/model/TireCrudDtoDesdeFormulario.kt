package com.rfz.appflotal.presentation.ui.registrollantasscreen.model

import com.rfz.appflotal.data.model.tire.dto.TireCrudDto
import com.rfz.appflotal.data.model.tire.response.TireIdResponse
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Arma el cuerpo de api/Tire/CrudTire. Con [fichaBloqueada] los datos de adquisición salen de
 * la ficha original: la llanta ya tiene inspección y no deben cambiar.
 * Los campos deben venir validados con [ValidadorFormularioLlanta].
 */
fun construirTireCrudDto(
    idLlanta: Int,
    campos: CamposLlanta,
    fichaBloqueada: TireIdResponse?,
    fechaRegistro: LocalDateTime,
): TireCrudDto = TireCrudDto(
    idTire = idLlanta,
    typeAcquisitionId = fichaBloqueada?.typeAcquisitionId
        ?: checkNotNull(campos.tipoAdquisicion).id,
    providerId = 0,
    productId = fichaBloqueada?.productId ?: checkNotNull(campos.producto).id,
    acquisitionDate = fichaBloqueada?.acquisitionDate
        ?: inicioDelDia(campos.fechaAdquisicion, fechaRegistro),
    document = campos.folioFactura.trim(),
    unitCost = fichaBloqueada?.unitCost ?: campos.costo.trim().toDouble().toInt(),
    dot = campos.dot.trim(),
    tireNumber = campos.numeroLlanta.trim(),
    treadDepth = fichaBloqueada?.treadDepth ?: campos.profundidad.trim().toDouble(),
    registrationDate = fechaRegistro.toString(),
    isActive = true,
    retreadDesignId = 0,
    destination = 2,
    lifecycle = 0,
)

// El backend espera la fecha a medianoche; si no se reconoce, se usa la del registro.
private fun inicioDelDia(fecha: String, respaldo: LocalDateTime): String = try {
    LocalDate.parse(fecha.trim().substringBefore('T'), DateTimeFormatter.ISO_LOCAL_DATE)
        .atStartOfDay()
        .toString()
} catch (_: Exception) {
    respaldo.toString()
}
