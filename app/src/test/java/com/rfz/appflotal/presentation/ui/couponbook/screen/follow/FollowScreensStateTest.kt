package com.rfz.appflotal.presentation.ui.couponbook.screen.follow

import androidx.work.WorkInfo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** Estado de "Buscar ahora" y mensajes de un solo uso de "Siguiendo". */
class FollowScreensStateTest {

    // --- "Buscar ahora" ---

    @Test
    fun refreshStatusFor_workInProgressOrFinished_mapsToVisibleStatus() {
        assertEquals(RefreshStatus.WAITING, refreshStatusFor(WorkInfo.State.ENQUEUED, true))
        assertEquals(RefreshStatus.WAITING, refreshStatusFor(WorkInfo.State.BLOCKED, true))
        assertEquals(RefreshStatus.SEARCHING, refreshStatusFor(WorkInfo.State.RUNNING, true))
        assertEquals(RefreshStatus.IDLE, refreshStatusFor(WorkInfo.State.SUCCEEDED, true))
        assertEquals(RefreshStatus.IDLE, refreshStatusFor(WorkInfo.State.CANCELLED, true))
        assertEquals(RefreshStatus.IDLE, refreshStatusFor(null, false))
    }

    @Test
    fun refreshStatusFor_failed_showsOnlyIfRequestedThisVisit() {
        assertEquals(RefreshStatus.FAILED, refreshStatusFor(WorkInfo.State.FAILED, true))
        assertEquals(RefreshStatus.IDLE, refreshStatusFor(WorkInfo.State.FAILED, false))
    }

    // --- Mensajes ---

    @Test
    fun withMessageShown_currentMessage_clearsIt() {
        val shown = FollowMessage.Unfollowed("Dash cam")
        val state = FollowPromotionUiState(message = shown)

        assertNull(state.withMessageShown(shown).message)
    }

    @Test
    fun withMessageShown_newerMessageArrived_keepsNewer() {
        val previous = FollowMessage.Unfollowed("Dash cam")
        val newer = FollowMessage.Error(FollowError.SaveFailed)
        val state = FollowPromotionUiState(message = newer)

        assertEquals(newer, state.withMessageShown(previous).message)
    }
}
