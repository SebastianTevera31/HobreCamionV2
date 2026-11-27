package com.rfz.appflotal.presentation.ui.registrollantasscreen.model

import com.rfz.appflotal.data.model.acquisitiontype.response.AcquisitionTypeResponse
import com.rfz.appflotal.data.model.product.response.ProductResponse

data class TipoAdquisicionUi(
    val id: Int,
    val descripcionEs: String,
    val descripcionEn: String,
) {
    fun descripcion(esEspanol: Boolean): String = if (esEspanol) descripcionEs else descripcionEn
}

data class ProductoUi(
    val id: Int,
    val descripcion: String,
    val profundidad: Int,
)

/** Valores capturados en el formulario; números como texto para respetar lo que escribe el usuario. */
data class CamposLlanta(
    val tipoAdquisicion: TipoAdquisicionUi? = null,
    val producto: ProductoUi? = null,
    /** Fecha ISO ("yyyy-MM-dd" con o sin hora). */
    val fechaAdquisicion: String = "",
    val costo: String = "",
    val folioFactura: String = "",
    val profundidad: String = "",
    val numeroLlanta: String = "",
    val dot: String = "",
)

enum class CampoLlanta {
    TIPO_ADQUISICION, PRODUCTO, FECHA, COSTO, PROFUNDIDAD, NUMERO_LLANTA, DOT
}

enum class ErrorCampo { REQUERIDO, MAYOR_A_CERO, EXCEDE_LONGITUD }

fun AcquisitionTypeResponse.toUi() = TipoAdquisicionUi(
    id = idAcquisitionType,
    descripcionEs = description,
    descripcionEn = enDescription,
)

fun ProductResponse.toUi() = ProductoUi(
    id = idProduct,
    descripcion = descriptionProduct,
    profundidad = treadDepth,
)
