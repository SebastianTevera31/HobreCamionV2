package com.rfz.appflotal.domain.tire

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TireEditPolicyTest {

    @Test
    fun `con el Wizard activo se editan los datos de adquisicion`() {
        assertTrue(TireEditPolicy.canEditAcquisitionData(isWizardActive = true))
    }

    @Test
    fun `sin Wizard y sin dato de inspeccion quedan bloqueados`() {
        assertFalse(TireEditPolicy.canEditAcquisitionData(isWizardActive = false))
    }

    @Test
    fun `sin Wizard pero sin inspeccion se pueden editar`() {
        assertTrue(TireEditPolicy.canEditAcquisitionData(isWizardActive = false, hasInspection = false))
    }

    @Test
    fun `sin Wizard y con inspeccion quedan bloqueados`() {
        assertFalse(TireEditPolicy.canEditAcquisitionData(isWizardActive = false, hasInspection = true))
    }

    @Test
    fun `el Wizard activo prevalece aunque haya inspeccion`() {
        assertTrue(TireEditPolicy.canEditAcquisitionData(isWizardActive = true, hasInspection = true))
    }
}
