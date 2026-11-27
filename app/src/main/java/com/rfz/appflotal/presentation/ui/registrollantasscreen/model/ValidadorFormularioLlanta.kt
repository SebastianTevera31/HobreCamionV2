package com.rfz.appflotal.presentation.ui.registrollantasscreen.model

/** Reglas para guardar una llanta con api/Tire/CrudTire. */
object ValidadorFormularioLlanta {

    const val MAX_NUMERO_LLANTA = 15
    const val MAX_DOT = 10

    /**
     * @param adquisicionEditable false cuando la llanta ya tiene inspección: esos datos se
     * reenvían de la ficha original y no se validan.
     * @return errores por campo; vacío si se puede guardar.
     */
    fun validar(campos: CamposLlanta, adquisicionEditable: Boolean): Map<CampoLlanta, ErrorCampo> =
        buildMap {
            when {
                campos.numeroLlanta.isBlank() ->
                    put(CampoLlanta.NUMERO_LLANTA, ErrorCampo.REQUERIDO)

                campos.numeroLlanta.trim().length > MAX_NUMERO_LLANTA ->
                    put(CampoLlanta.NUMERO_LLANTA, ErrorCampo.EXCEDE_LONGITUD)
            }
            if (campos.dot.trim().length > MAX_DOT) put(CampoLlanta.DOT, ErrorCampo.EXCEDE_LONGITUD)

            if (!adquisicionEditable) return@buildMap

            if (campos.tipoAdquisicion == null) put(CampoLlanta.TIPO_ADQUISICION, ErrorCampo.REQUERIDO)
            if (campos.producto == null) put(CampoLlanta.PRODUCTO, ErrorCampo.REQUERIDO)
            if (campos.fechaAdquisicion.isBlank()) put(CampoLlanta.FECHA, ErrorCampo.REQUERIDO)
            validarPositivo(campos.costo)?.let { put(CampoLlanta.COSTO, it) }
            validarPositivo(campos.profundidad)?.let { put(CampoLlanta.PROFUNDIDAD, it) }
        }

    private fun validarPositivo(valor: String): ErrorCampo? = when {
        valor.isBlank() -> ErrorCampo.REQUERIDO
        (valor.trim().toDoubleOrNull() ?: 0.0) <= 0.0 -> ErrorCampo.MAYOR_A_CERO
        else -> null
    }
}
