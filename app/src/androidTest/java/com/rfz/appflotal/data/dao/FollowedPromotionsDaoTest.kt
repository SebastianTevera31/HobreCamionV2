package com.rfz.appflotal.data.dao

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.rfz.appflotal.data.database.AppHombreCamionDatabase
import com.rfz.appflotal.data.model.promotions.FollowedAccessoryEntity
import com.rfz.appflotal.data.model.promotions.FollowedPromotionEntity
import com.rfz.appflotal.data.model.promotions.FollowedSeenProductEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/** Guardado de búsquedas de "Siguiendo" con la tabla de productos vistos. */
@RunWith(AndroidJUnit4::class)
class FollowedPromotionsDaoTest {

    private lateinit var database: AppHombreCamionDatabase
    private lateinit var dao: FollowedPromotionsDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            AppHombreCamionDatabase::class.java
        ).build()
        dao = database.followedPromotionsDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    private suspend fun follow(name: String): Long = dao.insertFollowedAccessory(
        FollowedAccessoryEntity(
            catalogAccessoryId = null,
            vehicleType = "TRUCK",
            name = name,
            normalizedName = name.lowercase(),
            description = null,
            createdAt = 0
        )
    )

    private fun promotion(accessoryId: Long, url: String, firstSeenAt: Long) =
        FollowedPromotionEntity(
            followedAccessoryId = accessoryId,
            productUrl = url,
            position = 0,
            store = "Store",
            title = "Item",
            sku = null,
            regularPrice = 100.0,
            finalPrice = 80.0,
            percentOff = 20.0,
            currency = "USD",
            imageUrl = null,
            firstSeenAt = firstSeenAt
        )

    private fun seen(accessoryId: Long, url: String, firstSeenAt: Long, lastSeenAt: Long) =
        FollowedSeenProductEntity(accessoryId, url, firstSeenAt, lastSeenAt)

    private suspend fun sync(accessoryId: Long, urls: List<String>, now: Long, forgetBefore: Long) =
        dao.applySync(
            followedAccessoryId = accessoryId,
            promotions = urls.map { promotion(accessoryId, it, now) },
            seen = urls.map { seen(accessoryId, it, now, now) },
            syncedAt = now,
            forgetSeenBefore = forgetBefore
        )

    @Test
    fun loQueSaleDeLosResultadosSigueComoVistoHastaVencer() = runBlocking {
        val id = follow("Dash cam")

        sync(id, listOf("a", "e"), now = 1_000, forgetBefore = 0)
        sync(id, listOf("a"), now = 2_000, forgetBefore = 500)

        // En caché solo lo de la última búsqueda; "e" sigue contando como visto.
        assertEquals(1, promotionsCount(id))
        assertEquals(setOf("a", "e"), dao.getKnownProductUrls().toSet())

        // Cuando su última vez visto queda antes del corte, se olvida.
        sync(id, listOf("a"), now = 3_000, forgetBefore = 1_500)
        assertEquals(setOf("a"), dao.getKnownProductUrls().toSet())
    }

    @Test
    fun noGuardaNadaSiSeDejoDeSeguirDuranteLaBusqueda() = runBlocking {
        val id = follow("Dash cam")
        dao.unfollow(id)

        sync(id, listOf("a"), now = 1_000, forgetBefore = 0)

        assertTrue(dao.getKnownProductUrls().isEmpty())
        assertEquals(0, promotionsCount(id))
    }

    @Test
    fun dejarDeSeguirBorraLoVistoDeEseAccesorio() = runBlocking {
        val dashCam = follow("Dash cam")
        val tpms = follow("TPMS")
        sync(dashCam, listOf("a"), now = 1_000, forgetBefore = 0)
        sync(tpms, listOf("b"), now = 1_000, forgetBefore = 0)

        dao.unfollow(dashCam)

        assertEquals(listOf("b"), dao.getKnownProductUrls())
        assertTrue(dao.getSeenProductsFor(dashCam).isEmpty())
    }

    private fun promotionsCount(accessoryId: Long): Int =
        database.query(
            "SELECT COUNT(*) FROM followed_promotion_table WHERE followedAccessoryId = ?",
            arrayOf(accessoryId)
        ).use { cursor ->
            cursor.moveToFirst()
            cursor.getInt(0)
        }
}
