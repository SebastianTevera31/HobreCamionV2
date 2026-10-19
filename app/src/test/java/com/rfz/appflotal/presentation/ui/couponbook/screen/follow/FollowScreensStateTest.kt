package com.rfz.appflotal.presentation.ui.couponbook.screen.follow

import androidx.work.WorkInfo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** Estado de "Buscar ahora" y mensajes de un solo uso de "Siguiendo". */
class FollowScreensStateTest {

    // --- "Buscar ahora" ---

    @Test
    fun `en cola se muestra en espera y en ejecucion buscando`() {
        assertEquals(RefreshStatus.WAITING, refreshStatusFor(WorkInfo.State.ENQUEUED, true))
        assertEquals(RefreshStatus.WAITING, refreshStatusFor(WorkInfo.State.BLOCKED, true))
        assertEquals(RefreshStatus.SEARCHING, refreshStatusFor(WorkInfo.State.RUNNING, true))
    }

    @Test
    fun `falla solo se muestra si se pidio en esta visita`() {
        assertEquals(RefreshStatus.FAILED, refreshStatusFor(WorkInfo.State.FAILED, true))
        assertEquals(RefreshStatus.IDLE, refreshStatusFor(WorkInfo.State.FAILED, false))
    }

    @Test
    fun `terminada o sin busqueda no muestra estado`() {
        assertEquals(RefreshStatus.IDLE, refreshStatusFor(WorkInfo.State.SUCCEEDED, true))
        assertEquals(RefreshStatus.IDLE, refreshStatusFor(WorkInfo.State.CANCELLED, true))
        assertEquals(RefreshStatus.IDLE, refreshStatusFor(null, false))
    }

    // --- Mensajes ---

    @Test
    fun `al terminar de mostrarse el mensaje se quita`() {
        val shown = FollowMessage.Unfollowed("Dash cam")
        val state = FollowPromotionUiState(message = shown)

        assertNull(state.withMessageShown(shown).message)
    }

    @Test
    fun `un mensaje que llego despues no se borra al terminar el anterior`() {
        val previous = FollowMessage.Unfollowed("Dash cam")
        val newer = FollowMessage.Error(FollowError.SaveFailed)
        val state = FollowPromotionUiState(message = newer)

        assertEquals(newer, state.withMessageShown(previous).message)
    }
}
