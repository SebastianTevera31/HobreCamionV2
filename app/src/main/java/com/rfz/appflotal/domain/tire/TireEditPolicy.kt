package com.rfz.appflotal.domain.tire

/**
 * Regla para editar los datos de adquisición de una llanta ya registrada (tipo de adquisición,
 * producto, fecha, costo y profundidad) con api/Tire/CrudTire. Número de llanta, DOT y folio
 * de factura siempre se pueden editar.
 *
 * La profundidad capturada es propia de la llanta: el backend la usa antes que la de
 * inspección y la del modelo, así que ajustarla no cambia otras llantas.
 *
 * El backend aplica la misma salvaguarda con el wizardActivo del usuario.
 */
object TireEditPolicy {

    /**
     * @param isWizardActive el usuario aún no completa el Wizard de configuración.
     * @param hasInspection si la llanta ya tiene una inspección (hasInspection de
     * api/Tire/GetTireById); null si no llegó, y entonces solo cuenta el Wizard.
     */
    fun canEditAcquisitionData(isWizardActive: Boolean, hasInspection: Boolean? = null): Boolean =
        isWizardActive || hasInspection == false
}
