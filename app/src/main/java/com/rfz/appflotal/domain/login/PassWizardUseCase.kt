package com.rfz.appflotal.domain.login

import com.rfz.appflotal.data.repository.database.HombreCamionRepository
import com.rfz.appflotal.data.repository.login.LoginRepository
import javax.inject.Inject

/**
 * Marca el Wizard de configuración como completado. Se guarda primero en Room para que no
 * vuelva a salir aunque no haya red; el aviso a api/PassedWizard queda pendiente hasta que
 * el servidor lo confirme, y [syncPending] lo reintenta.
 */
class PassWizardUseCase @Inject constructor(
    private val hombreCamionRepository: HombreCamionRepository,
    private val loginRepository: LoginRepository,
) {
    suspend operator fun invoke() {
        val user = hombreCamionRepository.getUserData() ?: return
        hombreCamionRepository.updatePassWizard(user.idUser, passed = true, pending = true)
        syncPending()
    }

    suspend fun syncPending() {
        val user = hombreCamionRepository.getUserData() ?: return
        if (!user.passWizardPending) return
        if (loginRepository.doPassedWizard()) {
            hombreCamionRepository.updatePassWizard(user.idUser, passed = true, pending = false)
        }
    }
}
